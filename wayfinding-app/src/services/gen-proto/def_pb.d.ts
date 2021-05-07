// package: rpc
// file: def.proto

import * as jspb from "google-protobuf";

export class ObjectResolveRequest extends jspb.Message {
  getFeatureid(): string;
  setFeatureid(value: string): void;

  getSkipifnotcached(): boolean;
  setSkipifnotcached(value: boolean): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ObjectResolveRequest.AsObject;
  static toObject(includeInstance: boolean, msg: ObjectResolveRequest): ObjectResolveRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ObjectResolveRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ObjectResolveRequest;
  static deserializeBinaryFromReader(message: ObjectResolveRequest, reader: jspb.BinaryReader): ObjectResolveRequest;
}

export namespace ObjectResolveRequest {
  export type AsObject = {
    featureid: string,
    skipifnotcached: boolean,
  }
}

export class ReturnedObject extends jspb.Message {
  getFeatureid(): string;
  setFeatureid(value: string): void;

  getObjectcontent(): Uint8Array | string;
  getObjectcontent_asU8(): Uint8Array;
  getObjectcontent_asB64(): string;
  setObjectcontent(value: Uint8Array | string): void;

  getFound(): boolean;
  setFound(value: boolean): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ReturnedObject.AsObject;
  static toObject(includeInstance: boolean, msg: ReturnedObject): ReturnedObject.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ReturnedObject, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ReturnedObject;
  static deserializeBinaryFromReader(message: ReturnedObject, reader: jspb.BinaryReader): ReturnedObject;
}

export namespace ReturnedObject {
  export type AsObject = {
    featureid: string,
    objectcontent: Uint8Array | string,
    found: boolean,
  }
}

export class ScanRegionRequest extends jspb.Message {
  getLat(): number;
  setLat(value: number): void;

  getLon(): number;
  setLon(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ScanRegionRequest.AsObject;
  static toObject(includeInstance: boolean, msg: ScanRegionRequest): ScanRegionRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ScanRegionRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ScanRegionRequest;
  static deserializeBinaryFromReader(message: ScanRegionRequest, reader: jspb.BinaryReader): ScanRegionRequest;
}

export namespace ScanRegionRequest {
  export type AsObject = {
    lat: number,
    lon: number,
  }
}

export class LocationAssociation extends jspb.Message {
  hasNodes(): boolean;
  clearNodes(): void;
  getNodes(): ObjectList | undefined;
  setNodes(value?: ObjectList): void;

  getLat(): number;
  setLat(value: number): void;

  getLon(): number;
  setLon(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): LocationAssociation.AsObject;
  static toObject(includeInstance: boolean, msg: LocationAssociation): LocationAssociation.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: LocationAssociation, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): LocationAssociation;
  static deserializeBinaryFromReader(message: LocationAssociation, reader: jspb.BinaryReader): LocationAssociation;
}

export namespace LocationAssociation {
  export type AsObject = {
    nodes?: ObjectList.AsObject,
    lat: number,
    lon: number,
  }
}

export class GetAssociatedObjectRequest extends jspb.Message {
  getFeatureid(): string;
  setFeatureid(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): GetAssociatedObjectRequest.AsObject;
  static toObject(includeInstance: boolean, msg: GetAssociatedObjectRequest): GetAssociatedObjectRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: GetAssociatedObjectRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): GetAssociatedObjectRequest;
  static deserializeBinaryFromReader(message: GetAssociatedObjectRequest, reader: jspb.BinaryReader): GetAssociatedObjectRequest;
}

export namespace GetAssociatedObjectRequest {
  export type AsObject = {
    featureid: string,
  }
}

export class ObjectList extends jspb.Message {
  clearFeatureidList(): void;
  getFeatureidList(): Array<string>;
  setFeatureidList(value: Array<string>): void;
  addFeatureid(value: string, index?: number): string;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ObjectList.AsObject;
  static toObject(includeInstance: boolean, msg: ObjectList): ObjectList.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ObjectList, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ObjectList;
  static deserializeBinaryFromReader(message: ObjectList, reader: jspb.BinaryReader): ObjectList;
}

export namespace ObjectList {
  export type AsObject = {
    featureidList: Array<string>,
  }
}

export class ObjectListWithAssociatedObjects extends jspb.Message {
  clearFeatureidList(): void;
  getFeatureidList(): Array<string>;
  setFeatureidList(value: Array<string>): void;
  addFeatureid(value: string, index?: number): string;

  getFeatureidandassociatedobjectsMap(): jspb.Map<string, ObjectList>;
  clearFeatureidandassociatedobjectsMap(): void;
  clearLocationassociationList(): void;
  getLocationassociationList(): Array<LocationAssociation>;
  setLocationassociationList(value: Array<LocationAssociation>): void;
  addLocationassociation(value?: LocationAssociation, index?: number): LocationAssociation;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ObjectListWithAssociatedObjects.AsObject;
  static toObject(includeInstance: boolean, msg: ObjectListWithAssociatedObjects): ObjectListWithAssociatedObjects.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ObjectListWithAssociatedObjects, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ObjectListWithAssociatedObjects;
  static deserializeBinaryFromReader(message: ObjectListWithAssociatedObjects, reader: jspb.BinaryReader): ObjectListWithAssociatedObjects;
}

export namespace ObjectListWithAssociatedObjects {
  export type AsObject = {
    featureidList: Array<string>,
    featureidandassociatedobjectsMap: Array<[string, ObjectList.AsObject]>,
    locationassociationList: Array<LocationAssociation.AsObject>,
  }
}

export class NameList extends jspb.Message {
  clearObjectnameList(): void;
  getObjectnameList(): Array<string>;
  setObjectnameList(value: Array<string>): void;
  addObjectname(value: string, index?: number): string;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): NameList.AsObject;
  static toObject(includeInstance: boolean, msg: NameList): NameList.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: NameList, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): NameList;
  static deserializeBinaryFromReader(message: NameList, reader: jspb.BinaryReader): NameList;
}

export namespace NameList {
  export type AsObject = {
    objectnameList: Array<string>,
  }
}

export class NameSearch extends jspb.Message {
  getKeyword(): string;
  setKeyword(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): NameSearch.AsObject;
  static toObject(includeInstance: boolean, msg: NameSearch): NameSearch.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: NameSearch, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): NameSearch;
  static deserializeBinaryFromReader(message: NameSearch, reader: jspb.BinaryReader): NameSearch;
}

export namespace NameSearch {
  export type AsObject = {
    keyword: string,
  }
}

export class RoutingDecision extends jspb.Message {
  getAssociateddataMap(): jspb.Map<string, string>;
  clearAssociateddataMap(): void;
  getVia(): string;
  setVia(value: string): void;

  getFrom(): string;
  setFrom(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RoutingDecision.AsObject;
  static toObject(includeInstance: boolean, msg: RoutingDecision): RoutingDecision.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RoutingDecision, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RoutingDecision;
  static deserializeBinaryFromReader(message: RoutingDecision, reader: jspb.BinaryReader): RoutingDecision;
}

export namespace RoutingDecision {
  export type AsObject = {
    associateddataMap: Array<[string, string]>,
    via: string,
    from: string,
  }
}

export class RoutingDecisionResp extends jspb.Message {
  clearHopsList(): void;
  getHopsList(): Array<RoutingDecision>;
  setHopsList(value: Array<RoutingDecision>): void;
  addHops(value?: RoutingDecision, index?: number): RoutingDecision;

  getCode(): number;
  setCode(value: number): void;

  getMsg(): string;
  setMsg(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RoutingDecisionResp.AsObject;
  static toObject(includeInstance: boolean, msg: RoutingDecisionResp): RoutingDecisionResp.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RoutingDecisionResp, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RoutingDecisionResp;
  static deserializeBinaryFromReader(message: RoutingDecisionResp, reader: jspb.BinaryReader): RoutingDecisionResp;
}

export namespace RoutingDecisionResp {
  export type AsObject = {
    hopsList: Array<RoutingDecision.AsObject>,
    code: number,
    msg: string,
  }
}

export class RoutingDecisionReq extends jspb.Message {
  hasFrom(): boolean;
  clearFrom(): void;
  getFrom(): RoutingDecisionReqLocation | undefined;
  setFrom(value?: RoutingDecisionReqLocation): void;

  hasTo(): boolean;
  clearTo(): void;
  getTo(): RoutingDecisionReqLocation | undefined;
  setTo(value?: RoutingDecisionReqLocation): void;

  getAdditionalinfoMap(): jspb.Map<string, string>;
  clearAdditionalinfoMap(): void;
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RoutingDecisionReq.AsObject;
  static toObject(includeInstance: boolean, msg: RoutingDecisionReq): RoutingDecisionReq.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RoutingDecisionReq, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RoutingDecisionReq;
  static deserializeBinaryFromReader(message: RoutingDecisionReq, reader: jspb.BinaryReader): RoutingDecisionReq;
}

export namespace RoutingDecisionReq {
  export type AsObject = {
    from?: RoutingDecisionReqLocation.AsObject,
    to?: RoutingDecisionReqLocation.AsObject,
    additionalinfoMap: Array<[string, string]>,
  }
}

export class RoutingDecisionReqLocation extends jspb.Message {
  getLat(): number;
  setLat(value: number): void;

  getLon(): number;
  setLon(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RoutingDecisionReqLocation.AsObject;
  static toObject(includeInstance: boolean, msg: RoutingDecisionReqLocation): RoutingDecisionReqLocation.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RoutingDecisionReqLocation, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RoutingDecisionReqLocation;
  static deserializeBinaryFromReader(message: RoutingDecisionReqLocation, reader: jspb.BinaryReader): RoutingDecisionReqLocation;
}

export namespace RoutingDecisionReqLocation {
  export type AsObject = {
    lat: number,
    lon: number,
  }
}

