import type {
  ActionRequest,
  SimulatorAction,
  SimulatorConfig,
  SimulatorOptions,
  SimulatorSnapshot,
} from "./types";

const API_BASE_URL = import.meta.env.VITE_API_URL?.replace(/\/$/, "") ?? "";

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, options);
  const body = await response.json();

  if (!response.ok) {
    throw new Error(body.message ?? `Request failed with status ${response.status}`);
  }

  return body as T;
}

export function getSnapshot() {
  return request<SimulatorSnapshot>("/api/simulator");
}

export function getOptions() {
  return request<SimulatorOptions>("/api/simulator/options");
}

export function resetSimulator(config: SimulatorConfig) {
  return request<SimulatorSnapshot>("/api/simulator/reset", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(config),
  });
}

export function performAction(action: SimulatorAction, body?: ActionRequest) {
  return request<SimulatorSnapshot>(`/api/simulator/actions/${action}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: body ? JSON.stringify(body) : undefined,
  });
}
