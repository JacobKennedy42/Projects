import Profile from "./Profile";

export default interface UserInfo {
  id: number;
  profile: Profile;
  admin: boolean;
  active: boolean;
  status: string;
}
