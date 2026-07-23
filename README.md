# Firearm State Machine Simulator

A full-stack teaching project built around a Java firearm state machine. The existing Java domain model remains the source of truth; Spring Boot exposes its transitions through a REST API, and React renders the current state and lets users trigger supported actions.

> This project is a software state-machine demonstration. It is not operational guidance for real firearms.

## Stack

- Java 17 and Maven
- Spring Boot 4
- React 19
- Vite
- TypeScript
- JUnit 5

## Architecture

```text
React UI (localhost:5173)
        |
        | JSON / REST
        v
Spring Boot API (localhost:8080)
        |
        v
Existing Java state machine
  Firearm / Magazine / Chamber / Bolt / Ammunition
```

The UI does not duplicate transition rules. Every action is sent to the backend, which invokes the original domain classes and returns a new immutable snapshot.

## Features

- Switch between closed-bolt and open-bolt simulations.
- Configure caliber, ammunition type, magazine capacity, and initial round count.
- Inspect magazine, chamber, bolt, and ammunition state in one responsive UI.
- Trigger firing, cycling, bolt, magazine, chamber, and malfunction-recovery actions.
- Review a short server-generated event history.
- Preserve the original state-machine tests and add service-level backend tests.

## Project Structure

```text
firearm_test/
├── frontend/                         # React + Vite + TypeScript
│   └── src/
│       ├── components/               # Reusable simulator panels and cards
│       ├── api.ts                    # Typed REST client
│       ├── types.ts                  # API contracts
│       └── App.tsx
├── src/main/java/
│   ├── FunctionClass/                # Existing state-machine domain model
│   └── com/firearm/simulator/
│       ├── api/                      # Request/response records
│       ├── config/                   # CORS configuration
│       ├── controller/               # REST endpoints and error handling
│       ├── model/                    # API-facing enums
│       └── service/                  # State-machine application service
├── src/main/resources/
│   └── application.properties
├── src/test/java/                    # Domain and Spring service tests
└── pom.xml
```

## Run Locally

Requirements:

- JDK 17 or newer
- Node.js 20.19+ or 22.12+ (required by Vite 8)

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

Open `http://localhost:5173`. During development, Vite proxies `/api` requests to the backend.

## API

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/api/simulator` | Get the current simulator snapshot |
| `GET` | `/api/simulator/options` | Get supported configuration values and actions |
| `POST` | `/api/simulator/reset` | Create a fresh simulator from a JSON configuration |
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

Supported actions are returned by `/api/simulator/options`, so the frontend does not need to maintain a separate list.

## Configuration

Backend:

- `PORT` changes the HTTP port; default is `8080`.
- `ALLOWED_ORIGINS` sets comma-separated browser origins; default is `http://localhost:5173`.

Frontend:

- `VITE_API_URL` can point the UI at a deployed API. When omitted, requests use `/api` and the local Vite proxy.

Example:

```powershell
$env:VITE_API_URL = "https://api.example.com/api"
npm run build
```

## Test and Build

Backend tests:

```powershell
.\mvnw.cmd test
```

Backend production package:

```powershell
.\mvnw.cmd package
java -jar target\firearm-test-1.0.0-SNAPSHOT.jar
```

Frontend checks and build:

```powershell
cd frontend
npm run lint
npm run build
```

The frontend production output is written to `frontend/dist`.

## Current Scope

The backend intentionally keeps one simulator session in memory. Restarting the backend resets it, and simultaneous users share the same state. This is a suitable first full-stack framework; authentication, per-user sessions, persistence, containerization, and cloud deployment can be added as separate, testable increments.

## Roadmap

- Add per-user simulator sessions.
- Persist saved configurations and transition histories.
- Add OpenAPI documentation and API-level integration tests.
- Containerize the frontend and backend.
- Deploy a small complete environment to AWS or Azure.
- Add more firearm state-machine types and richer malfunction states.
