export type FiringSystem = "CLOSED_BOLT" | "OPEN_BOLT";

export type SimulatorAction =
  | "FIRE"
  | "CYCLE"
  | "OPEN_BOLT"
  | "CLOSE_BOLT"
  | "LOAD_MAGAZINE_FMJ"
  | "LOAD_MAGAZINE_AP"
  | "LOAD_MAGAZINE_HP"
  | "LOAD_MAGAZINE_TO_CAPACITY"
  | "INSERT_MAGAZINE"
  | "REMOVE_MAGAZINE"
  | "CHAMBER_LOAD"
  | "CLEAR_MALFUNCTION";

export type SimulatorConfig = {
  firingSystem: FiringSystem;
  caliber: string;
  ammoType: string;
  magazineCapacity: number;
  initialRounds: number;
};

export type ActionRequest = {
  caliber?: string;
  ammoType?: string;
};

export type SimulatorEvent = {
  sequence: number;
  occurredAt: string;
  action: string;
  message: string;
};

export type SimulatorSnapshot = {
  firingSystem: FiringSystem;
  caliber: string;
  ammoType: string;
  malfunctioned: boolean;
  magazine: {
    inserted: boolean;
    state: string;
    capacity: number;
    rounds: number;
  };
  chamber: {
    state: string;
    ammunition: {
      caliber: string;
      ammoType: string;
      state: string;
    } | null;
  };
  bolt: {
    state: string;
  };
  events: SimulatorEvent[];
};

export type SimulatorOptions = {
  firingSystems: FiringSystem[];
  calibers: string[];
  ammoTypes: string[];
  actions: SimulatorAction[];
};
