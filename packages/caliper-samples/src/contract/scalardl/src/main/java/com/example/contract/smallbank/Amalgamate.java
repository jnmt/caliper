package com.example.contract.smallbank;

import com.scalar.ledger.asset.Asset;
import com.scalar.ledger.contract.Contract;
import com.scalar.ledger.exception.ContractContextException;
import com.scalar.ledger.ledger.Ledger;
import java.util.Optional;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import com.example.contract.smallbank.Const;

public class Amalgamate extends Contract {

  @Override
  public JsonObject invoke(Ledger ledger, JsonObject argument, Optional<JsonObject> properties) {
    String source_cId = "" + argument.getInt(Const.SRC_CID);
    String dest_cId = "" + argument.getInt(Const.DST_CID);

    Optional<Asset> s_asset = ledger.get(source_cId);
    if (!s_asset.isPresent()) {
      throw new ContractContextException(Const.ERR_NOT_FOUND);
    }
    Optional<Asset> d_asset = ledger.get(dest_cId);
    if (!d_asset.isPresent()) {
      throw new ContractContextException(Const.ERR_NOT_FOUND);
    }

    JsonObject s_data = s_asset.get().data();
    JsonObject d_data = d_asset.get().data();
    int src_s_balance = s_data.getInt(Const.S_BALANCE);
    int dst_c_balance = d_data.getInt(Const.C_BALANCE);
    dst_c_balance += src_s_balance;
    src_s_balance = 0;

    JsonObjectBuilder new_s_data = Json.createObjectBuilder();
    JsonObjectBuilder new_d_data = Json.createObjectBuilder();
    s_data.forEach(new_s_data::add);
    d_data.forEach(new_d_data::add);
    new_s_data.add(Const.S_BALANCE, src_s_balance);
    new_d_data.add(Const.C_BALANCE, dst_c_balance);
    ledger.put(source_cId, new_s_data.build());
    ledger.put(dest_cId, new_d_data.build());

    return null;
  }
}
