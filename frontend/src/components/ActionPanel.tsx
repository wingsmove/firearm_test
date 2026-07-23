import type { SimulatorAction, SimulatorSnapshot } from "../types";

type ActionPanelProps = {
  snapshot: SimulatorSnapshot;
  busy: boolean;
  onAction: (action: SimulatorAction) => void;
};

const PRIMARY_ACTIONS: Array<{ action: SimulatorAction; label: string }> = [
  { action: "FIRE", label: "Fire" },
  { action: "CYCLE", label: "Cycle action" },
  { action: "OPEN_BOLT", label: "Open bolt" },
  { action: "CLOSE_BOLT", label: "Close bolt" },
];

const SUPPORT_ACTIONS: Array<{ action: SimulatorAction; label: string }> = [
  { action: "LOAD_MAGAZINE_FMJ", label: "Load one FMJ round" },
  { action: "LOAD_MAGAZINE_AP", label: "Load one AP round" },
  { action: "LOAD_MAGAZINE_HP", label: "Load one HP round" },
  { action: "LOAD_MAGAZINE_TO_CAPACITY", label: "Fill magazine" },
  { action: "CHAMBER_LOAD", label: "Hand-load chamber" },
  { action: "CLEAR_MALFUNCTION", label: "Clear malfunction" },
];

function ActionPanel({ snapshot, busy, onAction }: ActionPanelProps) {
  const magazineAction: SimulatorAction = snapshot.magazine.inserted
    ? "REMOVE_MAGAZINE"
    : "INSERT_MAGAZINE";

  return (
    <section className="panel">
      <div className="section-heading">
        <div>
          <span className="eyebrow">Operations</span>
          <h2>Run the state machine</h2>
        </div>
        <span className={`system-badge${snapshot.malfunctioned ? " system-badge--warning" : ""}`}>
          {snapshot.malfunctioned ? "Malfunction" : "Operational"}
        </span>
      </div>

      <div className="action-group action-group--primary">
        {PRIMARY_ACTIONS.map(({ action, label }) => (
          <button key={action} disabled={busy} onClick={() => onAction(action)}>
            {label}
          </button>
        ))}
      </div>

      <div className="action-group">
        {SUPPORT_ACTIONS.map(({ action, label }) => (
          <button key={action} className="button-secondary" disabled={busy} onClick={() => onAction(action)}>
            {label}
          </button>
        ))}
        <button className="button-secondary" disabled={busy} onClick={() => onAction(magazineAction)}>
          {snapshot.magazine.inserted ? "Remove magazine" : "Insert magazine"}
        </button>
      </div>
    </section>
  );
}

export default ActionPanel;
