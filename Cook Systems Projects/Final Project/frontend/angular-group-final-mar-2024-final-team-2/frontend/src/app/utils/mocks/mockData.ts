import Profile from "../../models/Profile";
import UserInfo from "../../models/UserInfo";
import Team from "../../models/Team";
import Company from "../../models/Company";
import User from "../../models/User";
import Credentials from "../../models/Credentials";
import Project from "../../models/Project";

export const mockProfile: Profile = {
  email: "Testing@gmail.com",
  firstName: "Larry",
  lastName: "The Lobster",
  phone: "900909090909"
}

export const mockProfile2: Profile = {
  email: "com@com.com",
  firstName: "com",
  lastName: "com",
  phone: "000000000000"
}

export const mockProfile3: Profile = {
  email: "gmail@hotmail.com",
  firstName: "France",
  lastName: "Italianberg",
  phone: "8"
}

export const mockUserInfo: UserInfo = {
  admin: true,
  active: true,
  id: 1,
  profile: mockProfile,
  status: "Active"
}

export const mockTeams: Team = {
  description: "Team 1 Description",
  id: 1,
  name: "Team 1",
  teammates: [mockUserInfo],
  numberOfProjects: 3
}

export const mockUser: User = {
  id: 0,
  profile: mockProfile,
  admin: false,
  active: false,
  status: "PENDING",
  companies: [],
  teams: [mockTeams]
}

export const mockUser2: User = {
  id: 1,
  profile: mockProfile2,
  admin: true,
  active: true,
  status: "JOINED",
  companies: [],
  teams: [mockTeams]
}

export const mockUser3: User = {
  id: 2,
  profile: mockProfile3,
  admin: false,
  active: true,
  status: "PENDING",
  companies: [],
  teams: [mockTeams]
}

export const mockCompany : Company = {
  description: "Company 1 Description",
  id: 1,
  name: "Company 1",
  teams: [mockTeams],
  employees: [mockUser]
}

export const mockCompany2 : Company = {
  description: "Company 2 Description",
  id: 2,
  name: "Company 2",
  teams: [mockTeams],
  employees: [mockUser, mockUser2, mockUser3]
}

export const mockCredentials : Credentials = {
  "username": "cousingreg",
  "password": "mosteligiblebachelor"
}

export const mockProject: Project = {
  id: 1,
  name: "Project 1",
  description: "This is a mock project",
  active: true,
  team: mockTeams
};

export const loremIpsum = "Lorem ipsum our announcements go here! Our business has been going great and we are going to announce the winners of employee of the month! Lorem ipsum Lorem ipsum our announcements go here! Our business has been going great and we are going to announce the winners of employee of the month! Lorem ipsum  Lorem ipsum our announcements go here! Our business has been going great and we are going to announce the winners of employee of the month! Lorem ipsum "
