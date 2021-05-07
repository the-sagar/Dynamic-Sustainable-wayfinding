// package: rpc
// file: def.proto

var def_pb = require("./def_pb");
var grpc = require("@improbable-eng/grpc-web").grpc;

var RouteService = (function () {
  function RouteService() {}
  RouteService.serviceName = "rpc.RouteService";
  return RouteService;
}());

RouteService.Resolve = {
  methodName: "Resolve",
  service: RouteService,
  requestStream: false,
  responseStream: false,
  requestType: def_pb.ObjectResolveRequest,
  responseType: def_pb.ReturnedObject
};

RouteService.ScanRegion = {
  methodName: "ScanRegion",
  service: RouteService,
  requestStream: false,
  responseStream: false,
  requestType: def_pb.ScanRegionRequest,
  responseType: def_pb.ObjectListWithAssociatedObjects
};

RouteService.GetAssociatedObject = {
  methodName: "GetAssociatedObject",
  service: RouteService,
  requestStream: false,
  responseStream: false,
  requestType: def_pb.GetAssociatedObjectRequest,
  responseType: def_pb.ObjectList
};

RouteService.SearchByNamePrefix = {
  methodName: "SearchByNamePrefix",
  service: RouteService,
  requestStream: false,
  responseStream: false,
  requestType: def_pb.NameSearch,
  responseType: def_pb.NameList
};

RouteService.SearchByNameExact = {
  methodName: "SearchByNameExact",
  service: RouteService,
  requestStream: false,
  responseStream: false,
  requestType: def_pb.NameSearch,
  responseType: def_pb.ObjectList
};

RouteService.Route = {
  methodName: "Route",
  service: RouteService,
  requestStream: false,
  responseStream: false,
  requestType: def_pb.RoutingDecisionReq,
  responseType: def_pb.RoutingDecisionResp
};

exports.RouteService = RouteService;

function RouteServiceClient(serviceHost, options) {
  this.serviceHost = serviceHost;
  this.options = options || {};
}

RouteServiceClient.prototype.resolve = function resolve(requestMessage, metadata, callback) {
  if (arguments.length === 2) {
    callback = arguments[1];
  }
  var client = grpc.unary(RouteService.Resolve, {
    request: requestMessage,
    host: this.serviceHost,
    metadata: metadata,
    transport: this.options.transport,
    debug: this.options.debug,
    onEnd: function (response) {
      if (callback) {
        if (response.status !== grpc.Code.OK) {
          var err = new Error(response.statusMessage);
          err.code = response.status;
          err.metadata = response.trailers;
          callback(err, null);
        } else {
          callback(null, response.message);
        }
      }
    }
  });
  return {
    cancel: function () {
      callback = null;
      client.close();
    }
  };
};

RouteServiceClient.prototype.scanRegion = function scanRegion(requestMessage, metadata, callback) {
  if (arguments.length === 2) {
    callback = arguments[1];
  }
  var client = grpc.unary(RouteService.ScanRegion, {
    request: requestMessage,
    host: this.serviceHost,
    metadata: metadata,
    transport: this.options.transport,
    debug: this.options.debug,
    onEnd: function (response) {
      if (callback) {
        if (response.status !== grpc.Code.OK) {
          var err = new Error(response.statusMessage);
          err.code = response.status;
          err.metadata = response.trailers;
          callback(err, null);
        } else {
          callback(null, response.message);
        }
      }
    }
  });
  return {
    cancel: function () {
      callback = null;
      client.close();
    }
  };
};

RouteServiceClient.prototype.getAssociatedObject = function getAssociatedObject(requestMessage, metadata, callback) {
  if (arguments.length === 2) {
    callback = arguments[1];
  }
  var client = grpc.unary(RouteService.GetAssociatedObject, {
    request: requestMessage,
    host: this.serviceHost,
    metadata: metadata,
    transport: this.options.transport,
    debug: this.options.debug,
    onEnd: function (response) {
      if (callback) {
        if (response.status !== grpc.Code.OK) {
          var err = new Error(response.statusMessage);
          err.code = response.status;
          err.metadata = response.trailers;
          callback(err, null);
        } else {
          callback(null, response.message);
        }
      }
    }
  });
  return {
    cancel: function () {
      callback = null;
      client.close();
    }
  };
};

RouteServiceClient.prototype.searchByNamePrefix = function searchByNamePrefix(requestMessage, metadata, callback) {
  if (arguments.length === 2) {
    callback = arguments[1];
  }
  var client = grpc.unary(RouteService.SearchByNamePrefix, {
    request: requestMessage,
    host: this.serviceHost,
    metadata: metadata,
    transport: this.options.transport,
    debug: this.options.debug,
    onEnd: function (response) {
      if (callback) {
        if (response.status !== grpc.Code.OK) {
          var err = new Error(response.statusMessage);
          err.code = response.status;
          err.metadata = response.trailers;
          callback(err, null);
        } else {
          callback(null, response.message);
        }
      }
    }
  });
  return {
    cancel: function () {
      callback = null;
      client.close();
    }
  };
};

RouteServiceClient.prototype.searchByNameExact = function searchByNameExact(requestMessage, metadata, callback) {
  if (arguments.length === 2) {
    callback = arguments[1];
  }
  var client = grpc.unary(RouteService.SearchByNameExact, {
    request: requestMessage,
    host: this.serviceHost,
    metadata: metadata,
    transport: this.options.transport,
    debug: this.options.debug,
    onEnd: function (response) {
      if (callback) {
        if (response.status !== grpc.Code.OK) {
          var err = new Error(response.statusMessage);
          err.code = response.status;
          err.metadata = response.trailers;
          callback(err, null);
        } else {
          callback(null, response.message);
        }
      }
    }
  });
  return {
    cancel: function () {
      callback = null;
      client.close();
    }
  };
};

RouteServiceClient.prototype.route = function route(requestMessage, metadata, callback) {
  if (arguments.length === 2) {
    callback = arguments[1];
  }
  var client = grpc.unary(RouteService.Route, {
    request: requestMessage,
    host: this.serviceHost,
    metadata: metadata,
    transport: this.options.transport,
    debug: this.options.debug,
    onEnd: function (response) {
      if (callback) {
        if (response.status !== grpc.Code.OK) {
          var err = new Error(response.statusMessage);
          err.code = response.status;
          err.metadata = response.trailers;
          callback(err, null);
        } else {
          callback(null, response.message);
        }
      }
    }
  });
  return {
    cancel: function () {
      callback = null;
      client.close();
    }
  };
};

exports.RouteServiceClient = RouteServiceClient;

