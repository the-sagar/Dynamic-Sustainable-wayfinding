import { grpc } from "@improbable-eng/grpc-web";
import { WebsocketTransport } from "./websocket";
import { RouteServiceClient } from "services/gen-proto/def_pb_service";

class _RpcClient<T extends RouteServiceClient, K> {

  client: T | undefined;

  constructor(cls: new (serviceHost: string, options?: grpc.RpcOptions) => T, services: K, host?: string) {
    this.client = new cls(host||"http://localhost:9000", {transport: WebsocketTransport()});
    Object.keys(services).forEach(func => {
      //@ts-ignore
      RpcClient.prototype[func] = function (...args: any) {
        return new Promise((resolve, reject) => {
          //@ts-ignore
          this.client[func](...args, (err: any, resp: any) => {
            if (err) {
              reject(err);
            }
            resolve(resp);
          });
        });
      };
    });
  }
};

export const RpcClient = _RpcClient as ({
  new <T extends RouteServiceClient, K>(cls: new (serviceHost: string, options?: grpc.RpcOptions) => T, services: K, host?: string): _RpcClient<T, K> & K
});

type Fn = undefined | ((...args: any[]) => any);

export class TypeRouteServiceClient {
  resolve: Fn = undefined;
  scanRegion: Fn = undefined;
  getAssociatedObject: Fn = undefined;
  searchByNamePrefix: Fn = undefined;
  searchByNameExact: Fn = undefined;
  route: Fn = undefined;
}

export const rpcRouteServiceClient = new RpcClient(RouteServiceClient, new TypeRouteServiceClient());
