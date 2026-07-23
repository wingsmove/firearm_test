import type { ReactNode } from "react";

type StateCardProps = {
  label: string;
  state: string;
  children: ReactNode;
  warning?: boolean;
};

function StateCard({ label, state, children, warning = false }: StateCardProps) {
  return (
    <article className={`state-card${warning ? " state-card--warning" : ""}`}>
      <div className="state-card__heading">
        <span>{label}</span>
        <strong>{state.replaceAll("_", " ")}</strong>
      </div>
      <div className="state-card__body">{children}</div>
    </article>
  );
}

export default StateCard;
