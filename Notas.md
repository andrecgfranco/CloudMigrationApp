# Notas #

TL;DR: Kubernetes pods each get isolated network namespaces so they can all listen on :8080 simultaneously. Services provide DNS-based load balancing. Docker Compose shares the host's port namespace, so conflicts happen.

Sobre os namespaces:

```bash
# Inside cc2026 namespace
address: static://location-service:50051
# Resolves to: location-service.cc2026.svc.cluster.local

# Another namespace can have same name without conflict
address: static://location-service:50051
# Resolves to: location-service.other-namespace.svc.cluster.local
```

Namespaces podem deficnir resource quota:

```bash
# Only cc2026 namespace can use this much CPU/memory
apiVersion: v1
kind: ResourceQuota
metadata:
  name: cc2026-quota
  namespace: cc2026
spec:
  hard:
    requests.cpu: "10"      # Max 10 CPU cores
    requests.memory: "20Gi" # Max 20GB RAM

```

PORTS TÊM QUE ESTAR HARDCODED EM ALGUMAS PARTES DOS MANIFESTS

Envs agora definem maior part dos ports, config maps fazem esse trabalho para os manifests (na sua maioria)
Diminuida a quantiadade de CPU e memoria alocada
Liveliness probe alterada para atuar com menos frequência
