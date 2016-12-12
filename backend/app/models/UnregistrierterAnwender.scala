package models

import java.sql.SQLException
import javax.security.auth.login.CredentialException

import api.jwt.TokenPayload
import models.db.{ AnwenderEntity, DienstleistungsTypEntity, PK }
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.Future

/**
 * Created by David on 29.11.16.
 */
class UnregistrierterAnwender extends Base {

  import scala.concurrent.ExecutionContext.Implicits.global

  def anmelden(nutzerName: String, password: String): Future[AnwenderEntity] = {
    db.run(dal.getAnwenderByName(nutzerName)) map {
      anw: AnwenderEntity => if (BCrypt.checkpw(password, anw.password)) anw else throw new CredentialException("Invalid credentials.")
    } recover {
      case nf: NoSuchElementException => throw new CredentialException("Invalid credentials.")
    }
  }

  def anmeldenMitPayload(jwtPayload: TokenPayload): Anwender = {
    new Anwender(db.run(dal.getAnwenderById(new PK[AnwenderEntity](jwtPayload.userId))))
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

  def registrieren(anwender: AnwenderEntity) = {
    db.run(dal.insert(AnwenderEntity(anwender.nutzerEmail, BCrypt.hashpw(anwender.password, BCrypt.gensalt()), anwender.nutzerName)))
  }

  //Example CallByName Parameter
  def tryAndClose[A](block: => Future[A]) = try { block } finally { db.close }

  def registrieren(nutzerEmail: String, nutzerName: String, password: String): Future[AnwenderEntity] = {
    try {
      db.run(dal.insert(AnwenderEntity(nutzerEmail, BCrypt.hashpw(password, BCrypt.gensalt()), nutzerName)))
    } finally {
      db.close()
    }
  }

  //no idea where this goes so i'll put it here for now
  def getDienstleistungsTypen(limit: Long, offset: Long): Future[Seq[DienstleistungsTypEntity]] = {
    try {
      db.run(dal.getAllDlTs(limit, offset))
    } finally {
      db.close()
    }
  }

}