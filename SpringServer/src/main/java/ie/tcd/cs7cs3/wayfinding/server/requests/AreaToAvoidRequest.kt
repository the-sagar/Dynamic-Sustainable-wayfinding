package ie.tcd.cs7cs3.wayfinding.server.requests

class AreaToAvoidRequest(var expireTime: Long, var reason: String, var locationL: Float, var locationR: Float, var locationT: Float, var locationB: Float)
