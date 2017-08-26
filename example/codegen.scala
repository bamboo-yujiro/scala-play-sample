package example

object SlickCodegen {

  def main(args: Array[String]): Unit = {
    val slickDriver = "slick.driver.MySQLDriver"
    val jdbcDriver = "org.gjt.mm.mysql.Driver"
    val url = "jdbc:mysql://localhost/test?characterEncoding=UTF-8&amp;characterSetResults=UTF-8&amp;zeroDateTimeBehavior=convertToNull"
    val outputFolder = "src/main/scala"
    val pkg = "com.example.models"

    slick.codegen.SourceCodeGenerator.main(
      Array(
        slickDriver,
        jdbcDriver,
        url,
        outputFolder,
        pkg
      )
    )
  }
}
