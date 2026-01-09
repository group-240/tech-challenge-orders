terraform {
  required_version = ">= 1.0.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.23"
    }
  }

  backend "s3" {
    bucket         = "tech-challenge-tfstate-group240"
    key            = "orders/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    use_lockfile   = true  # S3 native locking (Terraform 1.10+)
    dynamodb_table = "tech-challenge-terraform-locks"  # Fallback for backwards compatibility
  }
}

provider "aws" {
  region = var.aws_region
}

# Remote state from infra
data "terraform_remote_state" "infra" {
  backend = "s3"
  config = {
    bucket = "tech-challenge-tfstate-group240"
    key    = "infra/terraform.tfstate"
    region = "us-east-1"
  }
}

# Remote state from RDS
data "terraform_remote_state" "rds" {
  backend = "s3"
  config = {
    bucket = "tech-challenge-tfstate-group240"
    key    = "rds/terraform.tfstate"
    region = "us-east-1"
  }
}

# Get EKS cluster auth
data "aws_eks_cluster_auth" "cluster" {
  name = data.terraform_remote_state.infra.outputs.eks_cluster_name
}

provider "kubernetes" {
  host                   = data.terraform_remote_state.infra.outputs.eks_cluster_endpoint
  cluster_ca_certificate = base64decode(data.terraform_remote_state.infra.outputs.eks_cluster_ca_certificate)
  token                  = data.aws_eks_cluster_auth.cluster.token
}
