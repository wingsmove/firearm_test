import type { SimulatorEvent } from "../types";

type EventLogProps = {
  events: SimulatorEvent[];
};

function EventLog({ events }: EventLogProps) {
  return (
    <section className="panel event-panel">
      <div className="section-heading">
        <div>
          <span className="eyebrow">Trace</span>
          <h2>Event history</h2>
        </div>
        <span className="event-count">{events.length} events</span>
      </div>
      <ol className="event-list">
        {events.map((event) => (
          <li key={event.sequence}>
            <span className="event-index">{String(event.sequence).padStart(2, "0")}</span>
            <div>
              <strong>{event.action.replaceAll("_", " ")}</strong>
              <p>{event.message}</p>
            </div>
            <time>{new Date(event.occurredAt).toLocaleTimeString()}</time>
          </li>
        ))}
      </ol>
    </section>
  );
}

export default EventLog;
