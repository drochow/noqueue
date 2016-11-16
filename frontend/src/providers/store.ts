
export class Store {
  id: number;
  name: string;
  address: string;
  description: string;
  openingHours: string;
  email: string;
  phone: string;

  constructor(id: number, name: string, address: string, description: string, openingHours: string, email: string, phone: string){
    this.id = id;
    this.name = name;
    this.address = address;
    this.description = description;
    this.openingHours = openingHours;
    this.email = email;
    this.phone = phone;
  }

  public getId(){
    return this.id;
  }

  public getName(){
    return this.name;
  }

  public getAddress(){
    return this.address;
  }

  public getDescription(){
    return this.description;
  }

  public getEmail(){
    return this.email;
  }

  public getOpeningHours(){
    return this.openingHours;
  }

  public getPhone(){
    return this.phone;
  }

}
