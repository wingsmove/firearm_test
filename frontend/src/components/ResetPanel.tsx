import { useEffect, useState, type FormEvent } from "react";
import type { SimulatorConfig, SimulatorOptions } from "../types";

type ResetPanelProps = {
  options: SimulatorOptions;
  busy: boolean;
  onReset: (config: SimulatorConfig) => Promise<void>;
};

const DEFAULT_CONFIG: SimulatorConfig = {
  firingSystem: "CLOSED_BOLT",
  caliber: "_9mm",
  ammoType: "FMJ",
  magazineCapacity: 17,
  initialRounds: 5,
};

function ResetPanel({ options, busy, onReset }: ResetPanelProps) {
  const [config, setConfig] = useState(DEFAULT_CONFIG);

  useEffect(() => {
    if (config.initialRounds > config.magazineCapacity) {
      setConfig((current) => ({ ...current, initialRounds: current.magazineCapacity }));
    }
  }, [config.initialRounds, config.magazineCapacity]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await onReset(config);
  }

  return (
    <section className="panel setup-panel">
      <div className="section-heading">
        <div>
          <span className="eyebrow">Configuration</span>
          <h2>Start a new session</h2>
        </div>
      </div>

      <form className="setup-form" onSubmit={handleSubmit}>
        <label>
          Firing system
          <select
            value={config.firingSystem}
            onChange={(event) => setConfig({ ...config, firingSystem: event.target.value as SimulatorConfig["firingSystem"] })}
          >
            {options.firingSystems.map((option) => (
              <option key={option} value={option}>{option.replace("_", " ")}</option>
            ))}
          </select>
        </label>
        <label>
          Caliber
          <select value={config.caliber} onChange={(event) => setConfig({ ...config, caliber: event.target.value })}>
            {options.calibers.map((option) => (
              <option key={option} value={option}>{option.replace("_", "")}</option>
            ))}
          </select>
        </label>
        <label>
          Ammunition
          <select value={config.ammoType} onChange={(event) => setConfig({ ...config, ammoType: event.target.value })}>
            {options.ammoTypes.map((option) => (
              <option key={option} value={option}>{option}</option>
            ))}
          </select>
        </label>
        <label>
          Magazine capacity
          <input
            type="number"
            min="1"
            max="100"
            value={config.magazineCapacity}
            onChange={(event) => setConfig({ ...config, magazineCapacity: Number(event.target.value) })}
          />
        </label>
        <label>
          Initial rounds
          <input
            type="number"
            min="0"
            max={config.magazineCapacity}
            value={config.initialRounds}
            onChange={(event) => setConfig({ ...config, initialRounds: Number(event.target.value) })}
          />
        </label>
        <button type="submit" disabled={busy}>Reset simulator</button>
      </form>
    </section>
  );
}

export default ResetPanel;
