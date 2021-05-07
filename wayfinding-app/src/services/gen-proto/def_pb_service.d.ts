// package: rpc
// file: def.proto

import * as def_pb from "./def_pb";
import {grpc} from "@improbable-eng/grpc-web";

type RouteServiceResolve = {
  readonly methodName: string;
  readonly service: typeof RouteService;
  readonly requestStream: false;
  readonly responseStream: false;
  readonly requestType: typeof def_pb.ObjectResolveRequest;
  readonly responseType: typeof def_pb.ReturnedObject;
};

type RouteServiceScanRegion = {
  readonly methodName: string;
  readonly service: typeof RouteService;
  readonly requestStream: false;
  readonly responseStream: false;
  readonly requestType: typeof def_pb.ScanRegionRequest;
  readonly responseType: typeof def_pb.ObjectListWithAssociatedObjects;
};

type RouteServiceGetAssociatedObject = {
  readonly methodName: string;
  readonly service: typeof RouteService;
  readonly requestStream: false;
  readonly responseStream: false;
  readonly requestType: typeof def_pb.GetAssociatedObjectRequest;
  readonly responseType: typeof def_pb.ObjectList;
};

type RouteServiceSearchByNamePrefix = {
  readonly methodName: string;
  readonly service: typeof RouteService;
  readonly requestStream: false;
  readonly responseStream: false;
  readonly requestType: typeof def_pb.NameSearch;
  readonly responseType: typeof def_pb.NameList;
};

type RouteServiceSearchByNameExact = {
  readonly methodName: string;
  readonly service: typeof RouteService;
  readonly requestStream: false;
  readonly responseStream: false;
  readonly requestType: typeof def_pb.NameSearch;
  readonly responseType: typeof def_pb.ObjectList;
};

type RouteServiceRoute = {
  readonly methodName: string;
  readonly service: typeof RouteService;
  readonly requestStream: false;
  readonly responseStream: false;
  readonly requestType: typeof def_pb.RoutingDecisionReq;
  readonly responseType: typeof def_pb.RoutingDecisionResp;
};

export class RouteService {
  static readonly serviceName: string;
  static readonly Resolve: RouteServiceResolve;
  static readonly ScanRegion: RouteServiceScanRegion;
  static readonly GetAssociatedObject: RouteServiceGetAssociatedObject;
  static readonly SearchByNamePrefix: RouteServiceSearchByNamePrefix;
  static readonly SearchByNameExact: RouteServiceSearchByNameExact;
  static readonly Route: RouteServiceRoute;
}

export type ServiceError = { message: string, code: number; metadata: grpc.Metadata }
export type Status = { details: string, code: number; metadata: grpc.Metadata }

interface UnaryResponse {
  cancel(): void;
}
interface ResponseStream<T> {
  cancel(): void;
  on(type: 'data', handler: (message: T) => void): ResponseStream<T>;
  on(type: 'end', handler: (status?: Status) => void): ResponseStream<T>;
  on(type: 'status', handler: (status: Status) => void): ResponseStream<T>;
}
interface RequestStream<T> {
  write(message: T): RequestStream<T>;
  end(): void;
  cancel(): void;
  on(type: 'end', handler: (status?: Status) => void): RequestStream<T>;
  on(type: 'status', handler: (status: Status) => void): RequestStream<T>;
}
interface BidirectionalStream<ReqT, ResT> {
  write(message: ReqT): BidirectionalStream<ReqT, ResT>;
  end(): void;
  cancel(): void;
  on(type: 'data', handler: (message: ResT) => void): BidirectionalStream<ReqT, ResT>;
  on(type: 'end', handler: (status?: Status) => void): BidirectionalStream<ReqT, ResT>;
  on(type: 'status', handler: (status: Status) => void): BidirectionalStream<ReqT, ResT>;
}

export class RouteServiceClient {
  readonly serviceHost: string;

  constructor(serviceHost: string, options?: grpc.RpcOptions);
  resolve(
    requestMessage: def_pb.ObjectResolveRequest,
    metadata: grpc.Metadata,
    callback: (error: ServiceError|null, responseMessage: def_pb.ReturnedObject|null) => void
  ): UnaryResponse;
  resolve(
    requestMessage: def_pb.ObjectResolveRequest,
    callback: (error: ServiceError|null, responseMessage: def_pb.ReturnedObject|null) => void
  ): UnaryResponse;
  scanRegion(
    requestMessage: def_pb.ScanRegionRequest,
    metadata: grpc.Metadata,
    callback: (error: ServiceError|null, responseMessage: def_pb.ObjectListWithAssociatedObjects|null) => void
  ): UnaryResponse;
  scanRegion(
    requestMessage: def_pb.ScanRegionRequest,
    callback: (error: ServiceError|null, responseMessage: def_pb.ObjectListWithAssociatedObjects|null) => void
  ): UnaryResponse;
  getAssociatedObject(
    requestMessage: def_pb.GetAssociatedObjectRequest,
    metadata: grpc.Metadata,
    callback: (error: ServiceError|null, responseMessage: def_pb.ObjectList|null) => void
  ): UnaryResponse;
  getAssociatedObject(
    requestMessage: def_pb.GetAssociatedObjectRequest,
    callback: (error: ServiceError|null, responseMessage: def_pb.ObjectList|null) => void
  ): UnaryResponse;
  searchByNamePrefix(
    requestMessage: def_pb.NameSearch,
    metadata: grpc.Metadata,
    callback: (error: ServiceError|null, responseMessage: def_pb.NameList|null) => void
  ): UnaryResponse;
  searchByNamePrefix(
    requestMessage: def_pb.NameSearch,
    callback: (error: ServiceError|null, responseMessage: def_pb.NameList|null) => void
  ): UnaryResponse;
  searchByNameExact(
    requestMessage: def_pb.NameSearch,
    metadata: grpc.Metadata,
    callback: (error: ServiceError|null, responseMessage: def_pb.ObjectList|null) => void
  ): UnaryResponse;
  searchByNameExact(
    requestMessage: def_pb.NameSearch,
    callback: (error: ServiceError|null, responseMessage: def_pb.ObjectList|null) => void
  ): UnaryResponse;
  route(
    requestMessage: def_pb.RoutingDecisionReq,
    metadata: grpc.Metadata,
    callback: (error: ServiceError|null, responseMessage: def_pb.RoutingDecisionResp|null) => void
  ): UnaryResponse;
  route(
    requestMessage: def_pb.RoutingDecisionReq,
    callback: (error: ServiceError|null, responseMessage: def_pb.RoutingDecisionResp|null) => void
  ): UnaryResponse;
}

