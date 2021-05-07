import {genRouteGeoJSON} from './geojson';
import { TypeNaviRoute } from 'services/rpc/types';
import dummy from './dummy.json';

describe("Test GeoJSON",()=>{
  it("conversion", async function (){
    const result = genRouteGeoJSON(dummy as TypeNaviRoute);
    console.log(JSON.stringify(result));
  })
})
