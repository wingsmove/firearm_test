# Firearm State Machine Simulator

A full-stack teaching project built around a Java firearm state machine. The
Java domain model remains the source of truth; Spring Boot exposes transitions
through a REST API, and React renders the current state and supported actions.

> This project is a software state-machine demonstration. It is not operational
> guidance for real firearms.

## Stack

- Java 17, Maven, Spring Boot 4, and JUnit 5
- React 19, Vite, and TypeScript
- Docker
- AWS CloudFormation
- GitHub Actions
- Amazon Lightsail Containers

## Architecture

During local development, Vite serves the UI on port 5173 and proxies `/api`
requests to Spring Boot on port 8080.

```text
React UI (localhost:5173)
        |
        | JSON / REST through the Vite proxy
        v
Spring Boot API (localhost:8080)
        |
        v
Java state machine
  Firearm / Magazine / Chamber / Bolt / Ammunition
```

In production, a multi-stage Docker build compiles React and copies its static
output into the Spring Boot JAR. One Lightsail container then serves both the
UI and REST API from the same HTTPS origin.

The UI does not duplicate transition rules. Every action is sent to the
backend, which invokes the original domain classes and returns an immutable
snapshot.

## Features

- Switch between closed-bolt and open-bolt simulations.
- Configure caliber, ammunition type, magazine capacity, and initial rounds.
- Inspect magazine, chamber, bolt, and ammunition state in one responsive UI.
- Trigger firing, cycling, bolt, magazine, chamber, and recovery actions.
- Review a short server-generated event history.
- Keep simulator state separate for each browser session without requiring login.
- Test the domain model, application service, and HTTP session behavior.
- Build and deploy automatically after validated changes reach `main`.

## Project Structure

```text
firearm_test/
|-- .github/workflows/              # CI and Lightsail deployment
|-- frontend/                       # React + Vite + TypeScript
|   `-- src/
|       |-- components/             # Reusable simulator panels and cards
|       |-- api.ts                  # Typed REST client
|       |-- types.ts                # API contracts
|       `-- App.tsx
|-- infra/
|   `-- github-oidc.yml             # GitHub-to-AWS identity infrastructure
|-- src/main/java/
|   |-- FunctionClass/              # Existing state-machine domain model
|   `-- com/firearm/simulator/
|       |-- api/                    # Request and response records
|       |-- config/                 # Local-development CORS configuration
|       |-- model/                  # API-facing enums
|       |-- service/                # Session-scoped application service
|       `-- web/                    # REST endpoints and error handling
|-- src/main/resources/
|   `-- application.properties
|-- src/test/java/                  # Domain, service, and HTTP integration tests
|-- .dockerignore
|-- Dockerfile
`-- pom.xml
```

## Run Locally

Requirements:

- JDK 17 or newer
- Node.js 20.19+ or 22.12+ for Vite 8

Start the backend from the repository root:

```powershell
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8080`.

In a second terminal, start the frontend:

```powershell
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. Vite proxies `/api` requests to the backend.

## API

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/api/simulator` | Get the current simulator snapshot |
| `GET` | `/api/simulator/options` | Get configuration values and actions |
| `POST` | `/api/simulator/reset` | Create a simulator from a JSON configuration |
| `POST` | `/api/simulator/actions/{action}` | Apply one state-machine action |

Example reset:

```json
{
  "firingSystem": "CLOSED_BOLT",
  "caliber": "_9mm",
  "ammoType": "FMJ",
  "magazineCapacity": 17,
  "initialRounds": 5
}
```

Supported actions are returned by `/api/simulator/options`, so the frontend
does not maintain a separate action list.

## Configuration

Backend:

- `PORT` changes the HTTP port; the default is `8080`.
- `ALLOWED_ORIGINS` configures browser origins for split local development;
  the default is `http://localhost:5173`.
- `SERVER_SERVLET_SESSION_TIMEOUT` controls inactive browser-session lifetime;
  the Lightsail deployment uses `30m`.

Frontend:

- `VITE_API_URL` can point the UI at a separately deployed API.
- When omitted, requests use `/api`; this is the production Docker behavior.

## Test and Build

Backend tests:

```powershell
.\mvnw.cmd test
```

Frontend checks:

```powershell
cd frontend
npm run lint
npm run build
```

Production container:

```powershell
docker build -t firearm-test:local .
docker run --rm -p 8080:8080 firearm-test:local
```

Open `http://localhost:8080`. The container serves both React and the API.

## AWS Deployment

The production target is one Amazon Lightsail Micro container node in
`us-east-2`. This keeps the architecture small: no database, separate frontend
host, load balancer, or long-lived AWS access keys.

The current Lightsail Micro list price is USD 10 per month. Eligible accounts
can receive the first three months free. Delete the container service when it
is no longer needed to stop its compute charge.

### One-time AWS identity bootstrap

The CloudFormation template creates:

- a GitHub OIDC provider;
- a role that only `wingsmove/firearm_test` on `main` can assume;
- a managed policy containing the specific Lightsail deployment actions.

```powershell
aws cloudformation deploy `
  --stack-name firearm-test-github-deploy `
  --template-file infra/github-oidc.yml `
  --capabilities CAPABILITY_NAMED_IAM `
  --region us-east-1
```

This identity stack has no monthly compute charge.

### Continuous delivery

Pull requests run `.github/workflows/ci.yml`, which performs:

1. deterministic frontend dependency installation;
2. TypeScript build and lint;
3. all Maven tests;
4. a complete production Docker build.

After review and merge, `.github/workflows/deploy-lightsail.yml` uses GitHub
OIDC to obtain short-lived AWS credentials. It creates the `firearm-test`
Lightsail service if necessary, uploads the commit-specific image, deploys it,
waits for its health check, and prints the HTTPS URL in the Actions summary.

To stop the Lightsail compute charge:

```powershell
aws lightsail delete-container-service `
  --service-name firearm-test `
  --region us-east-2
```

Deleting the Lightsail service does not delete the source code or the
CloudFormation identity stack.

## State and Persistence

Each browser session receives an independent `SimulatorService`. This prevents
visitors from changing one another's simulation while keeping the demo
login-free and database-free.

Session data is intentionally ephemeral:

- it survives normal page refreshes in the same browser session;
- it expires after inactivity;
- it is lost when the container restarts or is redeployed;
- it is not shared across multiple container nodes.

Persistent history, cross-device access, and horizontal scaling would require
an external state store in a future iteration.

## Roadmap

- Persist saved configurations and transition histories.
- Add OpenAPI documentation and more API-level integration tests.
- Add a custom domain after validating the deployment architecture.
- Add more state-machine types and richer malfunction states.
