package com.example.wochacha.entity;

import java.util.Date;
import java.util.Dictionary;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*{
 "keyType": "sample string 1",
 "product": {
 "productStatus": "sample string 1",
 "productStatusDescription": "sample string 2",
 "manufacturingDate": "2014-11-28T11:21:59.7801657+08:00",
 "expireDate": "2014-11-28T11:21:59.7801657+08:00",
 "productType": {
 "barCode": "sample string 1",
 "productName": "sample string 2",
 "imageUri": "sample string 3",
 "description": "sample string 4",
 "details": [
 {
 "Key": "sample string 1",
 "Value": "sample string 2"
 },
 {
 "Key": "sample string 1",
 "Value": "sample string 2"
 }
 ]
 }
 },
 "manufacturer": {
 "id": 1,
 "name": "sample string 2",
 "iconUri": "sample string 3",
 "isVerified": true,
 "details": "sample string 5"
 },
 "scanRecord": {
 "count": 1,
 "latest": [
 "sample string 1",
 "sample string 2"
 ]
 },
 "path": [
 "sample string 1",
 "sample string 2"
 ]
 }
 */
public class ScanResult implements JSONEntity {

	private String keyType;
	private ScanProduct product;
	private Manufacturer manufacturer;
	private ScanRecords scanRecord;
	private String[] path;
	
	
	public String getKeyType() {
		return keyType;
	}
	
	public ScanProduct getProduct() {
		return product;
	}
	
	public Manufacturer getManufacturer() {
		return manufacturer;
	}
	
	public ScanRecords getScanRecord() {
		return scanRecord;
	}
	
	public String[] getPath() {
		return path;
	}

	@Override
	public String toJsonString() throws JSONException {

		return null;
	}

	@Override
	public void populate(JSONObject object) {

	}

	public final static class ScanProduct implements JSONEntity {
		private String productStatus;
		private String productStatusDescription;
		private Date manufacturingDate;
		private Date expireDate;
		

		@Override
		public String toJsonString() throws JSONException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void populate(JSONObject object) {
			// TODO Auto-generated method stub

		}
	}

	public final static class ScanProductType implements JSONEntity {
		private String barCode;
		private String productName;
		private String imageUrl;
		private String description;
		private JSONArray details;

		@Override
		public String toJsonString() throws JSONException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void populate(JSONObject object) {
			// TODO Auto-generated method stub

		}
	}

	public final static class Manufacturer implements JSONEntity {
		private long id;
		private String name;
		private String iconUrl;
		private boolean isVerified;
		private String details;

		@Override
		public String toJsonString() throws JSONException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void populate(JSONObject object) {
			// TODO Auto-generated method stub

		}
	}

	public final static class ScanRecords implements JSONEntity {
		private int count;
		private String[] lastest;

		@Override
		public String toJsonString() throws JSONException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void populate(JSONObject object) {
			// TODO Auto-generated method stub

		}
	}

	public final static class Logistics {

	}

}
