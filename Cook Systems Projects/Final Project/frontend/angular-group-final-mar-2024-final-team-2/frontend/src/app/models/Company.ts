import Team from "./Team";
import User from "./User";

export default interface Company {
  id: number;
  name: string;
  description: string;
  teams: Team[];
  employees: User[];
}