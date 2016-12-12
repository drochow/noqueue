
export class User {
  id: number;
  username: string;
  email: string;
  password: string;

  constructor(id: number, username: string, email: string, password: string){
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
  }

  public getId(){
    return this.id;
  }

  public getUsername(){
    return this.username;
  }

  public getPassword(){
    return this.password;
  }

  public getEmail(){
    return this.email;
  }


}
