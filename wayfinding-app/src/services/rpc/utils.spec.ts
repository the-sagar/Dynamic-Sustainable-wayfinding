import { RouteGenerator } from "./utils";

describe("TestInit",()=>{
    jest.setTimeout(30000);
  it("init", async function (){
    let route = new RouteGenerator();
    let result = await route.getRoute({Lat:53.35214,Lon:-6.25866},{Lat:53.36135, Lon: -6.23813});
    console.log(JSON.stringify(result));
  })
})
