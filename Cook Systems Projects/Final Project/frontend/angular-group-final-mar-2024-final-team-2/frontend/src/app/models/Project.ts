import Team from "./Team";

export default interface Project {
  id?: number;
  name: string;
  description: string;
  active: boolean;
  team?: Team;
}

