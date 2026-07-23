import StateCard from "./StateCard";
import type { SimulatorSnapshot } from "../types";

type SimulatorStatusProps = {
  snapshot: SimulatorSnapshot;
};

function SimulatorStatus({ snapshot }: SimulatorStatusProps) {
  const chambered = snapshot.chamber.ammunition;

  return (
    <section className="status-grid" aria-label="Current component states">
      <StateCard
        label="Magazine"
        state={snapshot.magazine.state}
        warning={snapshot.magazine.state === "MALFUNCTIONED"}
      >
        <p>{snapshot.magazine.inserted ? "Inserted" : "Removed"}</p>
        <p>
          <strong>{snapshot.magazine.rounds}</strong> / {snapshot.magazine.capacity} rounds
        </p>
      </StateCard>

      <StateCard
        label="Chamber"
        state={snapshot.chamber.state}
        warning={snapshot.chamber.state === "MALFUNCTIONED"}
      >
        {chambered ? (
          <>
            <p>{chambered.caliber.replace("_", "")} · {chambered.ammoType}</p>
            <p>Round state: <strong>{chambered.state}</strong></p>
          </>
        ) : (
          <p>No round in chamber</p>
        )}
      </StateCard>

      <StateCard
        label="Bolt"
        state={snapshot.bolt.state}
        warning={snapshot.bolt.state === "MALFUNCTIONED"}
      >
        <p>{snapshot.firingSystem.replace("_", " ")} operating system</p>
        <p>Configured caliber: <strong>{snapshot.caliber.replace("_", "")}</strong></p>
      </StateCard>
    </section>
  );
}

export default SimulatorStatus;
