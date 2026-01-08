# Kubernetes Secret for database credentials
resource "kubernetes_secret" "db_credentials" {
  metadata {
    name      = "${var.app_name}-db-secret"
    namespace = var.namespace
  }

  data = {
    DB_HOST     = data.terraform_remote_state.rds.outputs.db_host
    DB_NAME     = data.terraform_remote_state.rds.outputs.db_name
    DB_USERNAME = var.db_username
    DB_PASSWORD = var.db_password
  }

  type = "Opaque"
}

# Kubernetes Deployment
resource "kubernetes_deployment" "app" {
  metadata {
    name      = "${var.app_name}-deployment"
    namespace = var.namespace
    labels = {
      app = var.app_name
    }
  }

  spec {
    replicas = var.replicas

    selector {
      match_labels = {
        app = var.app_name
      }
    }

    template {
      metadata {
        labels = {
          app = var.app_name
        }
      }

      spec {
        container {
          name  = var.app_name
          image = "${data.terraform_remote_state.infra.outputs.ecr_orders_url}:${var.image_tag}"

          port {
            container_port = var.container_port
          }

          env {
            name = "SPRING_DATASOURCE_URL"
            value = "jdbc:postgresql://${data.terraform_remote_state.rds.outputs.db_host}:5432/${data.terraform_remote_state.rds.outputs.db_name}"
          }

          env {
            name = "SPRING_DATASOURCE_USERNAME"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_credentials.metadata[0].name
                key  = "DB_USERNAME"
              }
            }
          }

          env {
            name = "SPRING_DATASOURCE_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_credentials.metadata[0].name
                key  = "DB_PASSWORD"
              }
            }
          }

          resources {
            limits = {
              cpu    = "500m"
              memory = "512Mi"
            }
            requests = {
              cpu    = "250m"
              memory = "256Mi"
            }
          }

          liveness_probe {
            http_get {
              path = "/actuator/health"
              port = var.container_port
            }
            initial_delay_seconds = 60
            period_seconds        = 10
          }

          readiness_probe {
            http_get {
              path = "/actuator/health"
              port = var.container_port
            }
            initial_delay_seconds = 30
            period_seconds        = 5
          }
        }
      }
    }
  }
}

# Kubernetes Service
resource "kubernetes_service" "app" {
  metadata {
    name      = "${var.app_name}-service"
    namespace = var.namespace
  }

  spec {
    selector = {
      app = var.app_name
    }

    port {
      port        = 80
      target_port = var.container_port
    }

    type = "ClusterIP"
  }
}
