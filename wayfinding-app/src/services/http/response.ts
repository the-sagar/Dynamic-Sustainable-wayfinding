export type LoginResponseType = {
  token: string,
  id: number,
  emailId: string,
  roles: string[],
  type: string
}

export type MessageResponseType = {
  responseCode: number,
  message: string
}

export type UnauthorizedResponseType = {
  error: string,
  message: string,
  path: string,
  status: number,
  timestamp: number,
  trace: string
}
