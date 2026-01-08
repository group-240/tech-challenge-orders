output "deployment_name" {
  description = "Kubernetes deployment name"
  value       = kubernetes_deployment.app.metadata[0].name
}

output "service_name" {
  description = "Kubernetes service name"
  value       = kubernetes_service.app.metadata[0].name
}

output "db_endpoint" {
  description = "RDS endpoint used by the app"
  value       = data.terraform_remote_state.rds.outputs.db_endpoint
}
