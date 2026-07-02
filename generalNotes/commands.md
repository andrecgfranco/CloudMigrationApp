# k8 Commands

## Namespaces

List all namespaces:

```bash
kubectl get namespaces
```

Get detailed namespace information:

```bash
kubectl describe namespaces <namespace-name>
```

## Pods

View pods in a specific namespace:

```bash
kubectl get pods -n <namespace-name>
```

View pods in all namespaces:

```bash
kubectl get pods --all-namespaces
```

Get detailed pod information:

```bash
kubectl describe pod <pod-name> -n <namespace-name>
```

Watch pods in real-time:

```bash
kubectl get pods -n <namespace-name> --watch
```

## Cleaning

To delete jobs:

```bash
kubectl delete job --all -n "$NAMESPACE"
```

To delete deployments:

```bash
kubectl delete deployment --all -n "$NAMESPACE"
```

To delete services:

```bash
delete service --all -n "$NAMESPACE"
```

## INGRESS

To check the ip (wait for atributtion by google cloud):

```bash
kubectl get ingress -n cc2026 services-ingress 
```

To forward a port to local host (example):

```bash
kubectl port-forward -n cc2026 svc/user-service 8081:8081
```

Delete the external IP to avoid hijacking:

```bash
kubectl delete ingress services-ingress -n cc2026
```

## Other

Authorize docker

```bash
gcloud auth configure-docker
```
