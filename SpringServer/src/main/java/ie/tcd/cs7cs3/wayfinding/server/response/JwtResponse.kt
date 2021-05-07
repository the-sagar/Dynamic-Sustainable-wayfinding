package ie.tcd.cs7cs3.wayfinding.server.response

class JwtResponse(var token: String, var id: Long, var emailId: String, var roles: List<String>) {
    var type = "Bearer"

}