# Deployment Instructions — auth-forum-service

**Autor:** Juliana Duarte (60807)
**Projecto:** Assistente de Migração Europeu
**Namespace:** `cc2026`
**Cluster:** `cc2026-cluster` (GKE, `europe-west1`)

---

## Pré-requisitos

Antes de fazer o deploy deste serviço, garante que tens instalado e configurado:

- [gcloud CLI](https://cloud.google.com/sdk/docs/install)
- [kubectl](https://kubernetes.io/docs/tasks/tools/)
- [Docker](https://docs.docker.com/get-docker/)

Autentica e configura o projecto GCP:

```bash
gcloud auth login
gcloud config set project cloud-computing-proj-2026
gcloud container clusters get-credentials cc2026-cluster \
  --region europe-west1 \
  --project cloud-computing-proj-2026
```

Autentica o Docker com o Artifact Registry:

```bash
gcloud auth configure-docker europe-west1-docker.pkg.dev
```

---

## Ordem de Deploy do Grupo

Este serviço depende de componentes deployed por outros membros do grupo.
**Respeitar esta ordem:**

```
1. dataBaseService    (DB partilhada + postgres-service)
2. locationService
3. userService        (gRPC na porta 50053 — dependência directa deste serviço)
4. searchService
5. auth-forum-service ← este serviço
6. ingress            (após todos os serviços estarem up)
```

---

## Dependências deste Serviço

| Dependência        | K8s Resource       | Porta | Notas                                                |
|--------------------|--------------------|-------|------------------------------------------------------|
| Base de dados       | `postgres-service` | 5432  | Deployed pelo membro responsável pelo dataBaseService |
| User Service (gRPC) | `user-service`     | 50053 | Necessário para auth funcionar                       |

> Se o `userService` não estiver up, as operações de registo, login e eliminação de conta falharão.
> Se o `dataBaseService` não estiver up, o serviço não fica disponível.

---

## Novidades da Fase 7

Esta fase introduziu as seguintes melhorias ao serviço:

### Google Secret Manager
As credenciais da base de dados são agora geridas pelo **Google Secret Manager** via **Workload Identity** — eliminando completamente o uso de K8s Secrets com valores em texto.

Os segredos já estão configurados no GCP:
- `db-name` — nome da base de dados
- `db-user` — utilizador da base de dados
- `db-password` — password da base de dados

O Service Account `auth-forum-sa` tem permissões de leitura sobre estes segredos e está configurado com Workload Identity.

### Monitorização com Prometheus + Grafana
O serviço expõe métricas em `/actuator/prometheus`. O `deploy.sh` faz deploy automático do Prometheus e Grafana no cluster.

Para aceder ao Grafana após o deploy:
```bash
kubectl port-forward svc/grafana 3000:3000 -n cc2026
# Abre http://localhost:3000 → admin / admin
# Data source: http://prometheus:9090
```

Para aceder ao Prometheus:
```bash
kubectl port-forward svc/prometheus 9090:9090 -n cc2026
# Abre http://localhost:9090
```

---

## API Exposta

### HTTP — porta 8082

| Método | Path                    | Descrição                               |
|--------|-------------------------|-----------------------------------------|
| POST   | `/api/v1/auth/register` | Registar novo utilizador                |
| POST   | `/api/v1/auth/login`    | Autenticar utilizador                   |
| DELETE | `/api/v1/auth/{id}`     | Eliminar conta                          |
| POST   | `/api/v1/forum/posts`   | Criar publicação no fórum               |
| GET    | `/api/v1/forum/posts`   | Listar publicações (ordem cronológica)  |

### gRPC — porta 50053

O auth-forum-service actua como **cliente** do `UserService` (Pedro):

| RPC                | Request                 | Response             |
|--------------------|-------------------------|----------------------|
| `CreateUser`       | `RegisterUserRequest`   | `User`               |
| `AuthenticateUser` | `RegisterUserRequest`   | `User`               |
| `DeleteUser`       | `UserDeleteRequest`     | `UserDeleteResponse` |
| `GetUserById`      | `GetUserByIdRequest`    | `User`               |

---

## Deploy

### 1. Verificar Secret Manager (apenas na primeira vez)

Os segredos já estão criados no GCP. Confirma que existem:

```bash
gcloud secrets list --project cloud-computing-proj-2026
```

Deves ver: `db-name`, `db-user`, `db-password`.

### 2. Build e Push da Imagem

Corre a partir da raiz do `auth-forum-service/`:

```bash
scripts/build-push.sh
```

Por defeito, a tag da imagem é gerada automaticamente a partir do **git commit hash**. Podes também passar uma tag manualmente:

```bash
scripts/build-push.sh <tag>   # ex: scripts/build-push.sh v1.0
```

As imagens são publicadas em:
```
europe-west1-docker.pkg.dev/cloud-computing-proj-2026/cc2026/auth-forum-service:<commit>
europe-west1-docker.pkg.dev/cloud-computing-proj-2026/cc2026/auth-forum-service:latest
```

### 3. Verificar Pré-requisitos no Cluster

```bash
# Verificar que o namespace existe
kubectl get namespace cc2026

# Verificar que o postgres-service está up
kubectl get service postgres-service -n cc2026

# Verificar que o user-service está up
kubectl get service user-service -n cc2026

# Verificar que o service account existe
kubectl get serviceaccount auth-forum-sa -n cc2026
```

### 4. Aplicar os Manifests

```bash
scripts/deploy.sh
```

O script aplica pela seguinte ordem:
1. `service-account.yaml` — Workload Identity para Secret Manager
2. `deployment.yaml` — deployment com referências ao Secret Manager
3. `service.yaml`
4. `hpa.yaml`
5. `monitoring/grafana-pvc.yaml`
6. `monitoring/prometheus-configmap.yaml`
7. `monitoring/prometheus-deployment.yaml`
8. `monitoring/prometheus-service.yaml`
9. `monitoring/grafana-deployment.yaml`
10. `monitoring/grafana-service.yaml`

> O rollout tem um timeout de 2 minutos. O serviço demora ~90 segundos a iniciar — é normal.

### 5. Verificar o Deploy

```bash
# Estado dos pods
kubectl get pods -n cc2026 -l app=auth-forum-service

# Logs do serviço
kubectl logs -n cc2026 -l app=auth-forum-service --tail=100

# Detalhes do deployment
kubectl describe deployment auth-forum-service -n cc2026
```

Os pods devem estar em estado `Running` com `READY 1/1`.

### 6. Testar o Serviço

Via port-forward:
```bash
kubectl port-forward svc/auth-forum-service 8082:8082 -n cc2026
```

Registo:
```bash
curl -i -X POST http://localhost:8082/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@test.com","name":"Test"}'
```

Via Ingress (se deployed):
```bash
curl -i -X POST http://<EXTERNAL-IP>/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@test.com","name":"Test"}'
```

---

## Rollback

Para reverter para a versão anterior:

```bash
scripts/rollback.sh
```

Para reverter para uma revisão específica:

```bash
scripts/rollback.sh <N>   # ex: scripts/rollback.sh 2
```

Para consultar o histórico de revisões:

```bash
kubectl rollout history deployment/auth-forum-service -n cc2026
```

---