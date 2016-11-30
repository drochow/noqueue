package models

import models.db.{ AnwenderEntity, DienstleistungsTypEntity }

import scala.concurrent.Future

/**
 * Created by David on 29.11.16.
 */
class UnregistrierterAnwender extends Base {

  def anmelden(nutzerEmail: String, password: String) = {
    //@todo implement me
    //    dal.getAnwenderByEmail
  }

  def anbieterSuchen(
    suchBegriff: String,
    dienstleistungen: Future[Seq[DienstleistungsTypEntity]],
    longitude: Double,
    latitude: Double,
    umkreisM: Int
  ) = {
    throw new NotImplementedError("Not implemented yet, implement it")
    //@todo implement me
  }

  def registrieren(nutzerEmail: String, nutzerName: String, password: String): Future[AnwenderEntity] = {

    throw new NotImplementedError("Not implemented yet, implement it")
    //@todo implement me and return Future[AnwenderEntity]
  }
}
