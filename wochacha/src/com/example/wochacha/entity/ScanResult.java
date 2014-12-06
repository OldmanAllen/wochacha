package com.example.wochacha.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.wochacha.util.StringHelper;

import android.R.string;
import android.text.format.DateFormat;

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
	private String url = "http://biz.cli.im/test/CI25850";

	public ScanResult() {
		product = new ScanProduct();
		manufacturer = new Manufacturer();
		scanRecord = new ScanRecords();

	}

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

	public String getUrl() {
		return url;
	}

	@Override
	public String toJsonString() throws JSONException {

		return null;
	}

	@Override
	public void populate(JSONObject object) {
		keyType = object.optString("keyType", null);
		JSONObject productObject = object.optJSONObject("product");
		if (productObject != null) {
			product.populate(productObject);
		}
		JSONObject manufacturerObject = object.optJSONObject("manufacturer");
		if (manufacturerObject != null) {
			manufacturer.populate(manufacturerObject);
		}
		JSONObject scanRecordObject = object.optJSONObject("scanRecord");
		if (scanRecordObject != null) {
			scanRecord.populate(scanRecordObject);
		}
		JSONArray pathArray = object.optJSONArray("path");
		if (pathArray != null) {
			path = StringHelper.toStringArray(pathArray);
		}
	}

	public boolean isDataValid() {
		return manufacturer.getId() > 0
				&& product.getProductStatusCode() != null;
	}

	public final static class ScanProduct implements JSONEntity {
		private String productStatusCode;
		private String productStatusDescription;
		private String productInformation;
		private Date manufacturingDate;
		private Date expireDate;
		private ScanProductType productType;

		public ScanProduct() {
			productType = new ScanProductType();
		}

		public String getProductStatusCode() {
			return productStatusCode;
		}

		public String getProductStatusDescription() {
			return productStatusDescription;
		}

		public String getProductInformation() {
			return productInformation;
		}

		public Date getManufacturingDate() {
			return manufacturingDate;
		}

		public Date getExpireDate() {
			return expireDate;
		}

		public ScanProductType getProductType() {
			return productType;
		}

		@Override
		public String toJsonString() throws JSONException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void populate(JSONObject object) {
			productStatusCode = object.optString("productStatusCode");
			productStatusDescription = object
					.optString("productStatusDescription");
			productInformation = object.optString("productInformation");
			String manufacturerDateString = object
					.optString("manufacturingDate");
			if (StringHelper.isStringNullOrEmpty(manufacturerDateString)) {
				manufacturingDate = StringHelper
						.convertStringToDate(manufacturerDateString);
			}
			String expireDateString = object.optString("expireDate");
			if (StringHelper.isStringNullOrEmpty(expireDateString)) {
				expireDate = StringHelper.convertStringToDate(expireDateString);
			}
			JSONObject productTypeObject = object.optJSONObject("productType");
			if (productTypeObject != null) {
				productType.populate(productTypeObject);
			}

		}
	}

	public final static class ScanProductType implements JSONEntity {
		private String barCode;
		private String productName;
		private String imageUri;
		private String description;
		private String details;

		public String getBarCode() {
			return barCode;
		}

		public String getProductName() {
			return productName;
		}

		public String getImageUri() {
			return imageUri;
		}

		public String getDescription() {
			return description;
		}

		public String getDetails() {
			return details;
		}

		@Override
		public String toJsonString() throws JSONException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void populate(JSONObject object) {
			barCode = object.optString("barCode");
			productName = object.optString("productName");
			imageUri = object.optString("imageUri");
			description = object.optString("description");
			details = object.optString("details");

		}
	}

	public final static class Manufacturer implements JSONEntity {
		private int id;
		private String name;
		private String imageUri;
		private boolean isVerified;
		private String description;
		private String details;

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getImageUri() {
			return imageUri;
		}

		public boolean isVerified() {
			return isVerified;
		}

		public String getDescription() {
			return description;
		}

		public String getDetails() {
			return details;
		}

		@Override
		public String toJsonString() throws JSONException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void populate(JSONObject object) {
			id = object.optInt("id");
			name = object.optString("name");
			imageUri = object.optString("imageUri");
			isVerified = object.optBoolean("isVerified");
			description = object.optString("description");
			details = object.optString("details");

		}
	}

	public final static class ScanRecords implements JSONEntity {
		private int count;
		private String[] latest;

		public int getCount() {
			return count;
		}

		public String[] getLatest() {
			return latest;
		}

		@Override
		public String toJsonString() throws JSONException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void populate(JSONObject object) {
			count = object.optInt("count");
			JSONArray array = object.optJSONArray("latest");
			if (array != null) {
				latest = StringHelper.toStringArray(array);
			}
		}

		
	}
}
