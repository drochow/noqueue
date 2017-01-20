package models.db

import slick.lifted.MappedTo

/**
 * Every Entity has got a Primary Key of Type Long, we use this case class to distungish between diffrent PK's
 *
 * @param value
 * @tparam Entity
 */
final case class PK[Entity](value: Long) extends AnyVal with MappedTo[Long]