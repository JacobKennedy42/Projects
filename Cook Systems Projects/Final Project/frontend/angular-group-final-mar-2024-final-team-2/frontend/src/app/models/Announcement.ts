import UserInfo from "./UserInfo";

export default interface Announcement {
  id: number;
  date: Date;
  title: string;
  message: string;
  authorFirstName: string;
  author: UserInfo;
}