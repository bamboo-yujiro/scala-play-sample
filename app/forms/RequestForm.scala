package forms

case class RequestForm(
  username: (String, String),
  password: String
)

