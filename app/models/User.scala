package models

import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._


case class User (username: String, password: String,var stories: List[Story]){
    def this(username: String, password: String) = this(username, password, List())

    def getStories():List[Story] = {
      StoryDAO.all.filter(s=> s.author == this.username)
    }

    def hasRated( story: Story): Boolean ={
    StoryRatedDAO.exists(this,story)
   }


    def addStory(story: Story): Unit ={
    this.stories = this.stories.:+(story)
    }


  def myStoriesRated(): List[StoryRated] ={
    println("HISTORIAS PUNTUADAS POR: " + this.username)
    StoryRatedDAO.getStoriesRatedOfUser(this)

  }

}

object UserDAO{


  def all(): List[User] = DB.withConnection { implicit c =>
    SQL("select * from user").as(user *)

  }
  def create(username: String, password: String): Int ={
    DB.withConnection { implicit c =>
      SQL("insert into user values ({username}, {password})").on(
        'username -> username, 'password -> password).executeUpdate()
    }

  }
  def delete (username: String) ={
    DB.withConnection { implicit c =>
      SQL("delete from user where username = {username}").on(
        'username -> username).executeUpdate()

    }
  }

  val user= {
    get[String]("username") ~ get[String]("password")  map {
      case username~password => new User(username, password)
    }
  }

  def getByName(username: String): User ={
    val users = all()
    users.filter(u => u.username == username).head
  }


}
