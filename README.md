# Migration Assistant Project

Deciding where to emigrate is hard. Cost of living, safety, healthcare, air quality and job prospects vary widely from country to country, and the information is scattered across many sources. The **Migration Assistant** brings it together in one place: it lets people explore, compare and rank European countries using official statistics (mostly from Eurostat and the European Environment Agency), so they can make an informed choice about their next destination. Users can also build a profile with their own priorities and share their experiences in a forum.

This is the source code repository for the project. 

This project implements the backend of a cloud-based (Google Cloud Platform) Migration Assistant App using Java and technologies such as SpringBoot, Docker, Kubernetes and PostgreSQL.

## Workflows

Whenever a change to one of the services is pushed to the repository, GitHub Actions runs the tests for that service (if applicable). If the tests pass, a new Docker image for the service is built and pushed to the Artifact Registry. If any test fails, the previous image is kept.

## Database

A PostgreSQL database was implemented. This includes a container running the database and a Python script to import dataset data into it.

## Auth-Forum Service

Handles user registration, login (mock authentication), and account deletion. Also manages forum functionality. Communicates via gRPC with the User Service. Implements request/log tracking through Prometheus and Grafana.

## Location Service

Handles all methods related to country information and associated metrics stored in the database. Acts as a gRPC server for the User Service to retrieve information. Since location data is largely static (no POST, PUT, PATCH, or DELETE methods), this service implements a cache-aside pattern.

## Search Service - Deprecated

Serves as a gRPC server supporting a User Service method.

## User Service

Contains the methods for searching and customizing user profiles. Acts as a gRPC server for the Auth-Forum service to retrieve user information, and consumes the Location Service's gRPC server for location data, as well as the Search Service for metrics.

## General Notes Folder

Contains a list of useful commands for interacting with and exploring the application.

## Notes

Every service should include a `.env` file in its respective folder, following the `.env.example` format.
Ports defined in the env files must match the ports defined in the manifests/configmaps in the scripts repository.
