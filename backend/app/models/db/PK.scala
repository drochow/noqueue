package models.db

import slick.lifted.MappedTo

final case class PK[A](value: Long) extends AnyVal with MappedTo[Long]