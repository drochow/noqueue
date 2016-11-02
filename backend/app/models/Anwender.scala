package models

import slick.lifted.TableQuery

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
//
//object AnwenderDAO extends TableQuery(Anwender) { //@TODO Sean
//  def getById(): Anwender
//  def getByNutzerEmail(): Anwender
//  def getByNutzerName(): Anwender
//  def save(anwender: Anwender)
//  def delete(anwender: Anwender)
//}