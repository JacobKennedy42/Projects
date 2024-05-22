import UserInfo from "./UserInfo";

export default interface Team {
  id: number;
  name: string;
  description: string;
  teammates: UserInfo[];
  numberOfProjects: number;
}
