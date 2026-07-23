import { useCallback, useEffect, useState } from "react";
import { getOptions, getSnapshot, performAction, resetSimulator } from "./api";
import ActionPanel from "./components/ActionPanel";
import EventLog from "./components/EventLog";
import ResetPanel from "./components/ResetPanel";
import SimulatorStatus from "./components/SimulatorStatus";
import type {
  SimulatorAction,
  SimulatorConfig,
  SimulatorOptions,
  SimulatorSnapshot,
} from "./types";

function App() {
  const [snapshot, setSnapshot] = useState<SimulatorSnapshot | null>(null);
  const [options, setOptions] = useState<SimulatorOptions | null>(null);
  const [busy, setBusy] = useState(true);
  const [error, setError] = useState("");

  const initialize = useCallback(async () => {
    setBusy(true);
    setError("");
    try {
      const [nextSnapshot, nextOptions] = await Promise.all([
        getSnapshot(),
        getOptions(),
      ]);
      setSnapshot(nextSnapshot);
      setOptions(nextOptions);
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : String(requestError));
    } finally {
      setBusy(false);
    }
  }, []);

  useEffect(() => {
    void initialize();
  }, [initialize]);

  async function runAction(action: SimulatorAction) {
    setBusy(true);
    setError("");
    try {
      setSnapshot(await performAction(action));
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : String(requestError));
    } finally {
      setBusy(false);
    }
  }

  async function reset(config: SimulatorConfig) {
    setBusy(true);
    setError("");
    try {
      setSnapshot(await resetSimulator(config));
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : String(requestError));
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="app-shell">
      <header className="hero">
        <div>
          <span className="eyebrow">Java state machine · Spring Boot · React</span>
          <h1>Firearm State Lab</h1>
          <p>
            Explore component transitions through a REST interface while the Java domain model remains the source of truth.
          </p>
        </div>
        <div className="hero-mark" aria-hidden="true">
          <span>FSM</span>
        </div>
      </header>

      {error ? (
        <div className="error-banner" role="alert">
          <strong>Request failed.</strong> {error}
          <button onClick={() => void initialize()}>Retry</button>
        </div>
      ) : null}

      {snapshot && options ? (
        <>
          <SimulatorStatus snapshot={snapshot} />
          <div className="workspace-grid">
            <ActionPanel snapshot={snapshot} busy={busy} onAction={(action) => void runAction(action)} />
            <ResetPanel options={options} busy={busy} onReset={reset} />
          </div>
          <EventLog events={snapshot.events} />
        </>
      ) : (
        <section className="loading-panel">{busy ? "Connecting to the simulator…" : "Simulator unavailable."}</section>
      )}

      <footer>
        Educational software model only. The interface visualizes program state and is not operational guidance.
      </footer>
    </main>
  );
}

export default App;
