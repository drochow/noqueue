package models

/**
  * Created by anwender on 02.11.2016.
  */
case class Anwender(
    anwenderId: Long,
    nutzerEmail: String,
    password: String,
    nutzerName: String,
    adresse: Adresse
) {
}

object AnwenderDAO extends TableQuery(){ #TODO Sean
  def getById(): Anwender
  def getByNutzerEmail(): Anwender
  def getByNutzerName(): Anwender
  def save(anwender: Anwender)
  def delete(anwender: Anwender)
}