package com.example.wochacha.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.wochacha.util.StringHelper;

import android.R.string;
import android.text.format.DateFormat;
import android.text.method.DateTimeKeyListener;

/*
 {
 "keyType": "PUBLIC_QR",
 "product": {
 "productStatusCode": "FAKE",
 "productStatusDescription": "本产品已被别人扫描，有可能为假冒",
 "productInformation": "",
 "manufacturingDate": "2014-12-11T00:00:00",
 "expireDate": "2064-12-08T00:00:00",
 "productType": {
 "barCode": "520015010001",
 "productName": "茅台",
 "imageUri": "http://web.local/1.png",
 "description": "",
 "details": "△配料表：酒精，水等\r\n△产地：贵州省仁怀市茅台镇\r\n△使用方法：直接饮用。\r\n△执行标准：Q/NKJN308S-2013\r\n△生产许可证号：QS370106010930\r\n△酒精纯度: 高度白酒（50%以上）\r\n△规格：500ML\r\n△净含量：450克\r\n△保质期：18260 天\r\n△存储方法：用后及密封，置于阴凉干燥处\r\n△生产日期：见罐底"
 }
 },
 "manufacturer": {
 "id": 2,
 "name": "贵州茅台酒股份有限公司",
 "imageUri": "http://wweb.chinacloudapp.cn/admin/images/maotai.jpg",
 "isVerified": true,
 "description": "贵州茅台酒股份有限公司",
 "details": ""
 },
 "scanRecord": {
 "count": 10,
 "latest": [
 "2014-12-11 17:02:26 通过手机APP扫描, 地点[浙江省杭州市西湖区]",
 "2014-12-11 17:02:23 通过手机APP扫描, 地点[浙江省杭州市西湖区]",
 "2014-12-11 17:02:20 通过手机APP扫描, 地点[浙江省杭州市西湖区]",
 "2014-12-11 17:02:08 通过手机APP扫描, 地点[浙江省杭州市西湖区]",
 "2014-12-11 17:01:57 通过手机APP扫描, 地点[浙江省杭州市西湖区]",
 "2014-12-11 16:55:27 通过手机APP扫描, 地点[浙江省杭州市西湖区]",
 "2014-12-11 16:54:41 通过手机APP扫描, 地点[浙江省杭州市西湖区]",
 "2014-12-11 16:54:31 通过手机APP扫描, 地点[浙江省杭州市西湖区]",
 "2014-12-11 16:54:20 通过手机APP扫描, 地点[浙江省杭州市西湖区]",
 "2014-12-11 16:54:16 通过手机APP扫描, 地点[浙江省杭州市西湖区]"
 ]
 },
 "path": [
 "2014-11-01 09:34:28 离开杭州市萧山XXX工厂",
 "2014-11-01 09:51:13 进入A仓库",
 "2014-11-02 11:23:04 离开A仓库",
 "2014-11-02 15:37:41 进入杭州市B仓库",
 "2014-11-05 08:10:53 离开杭州市B仓库",
 "2014-11-05 10:43:31 进入XX超市，上架销售"
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

	public List<ScanPath> getPathList() {
		if (path == null || path.length == 0) {
			return null;
		}
		List<ScanPath> list = new ArrayList<ScanPath>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (String row : path) {
			String[] columns = row.split("\\s");
			if (columns != null && columns.length > 2) {
				try {
					String dateString = columns[0] + " " + columns[1];
					Date date = format.parse(dateString);
					String pathInfo = row.replaceAll(dateString, "").trim();
					list.add(new ScanPath(pathInfo, date));

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		return list;

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
		return manufacturer.getId() > 0 && product.getProductStatusCode() != null;
	}

	public static class ScanPath {
		private Date logDate;
		private String pathInfo;

		public ScanPath(String pathInfo, Date logDate) {
			this.pathInfo = pathInfo;
			this.logDate = logDate;
		}

		public Date getLogDate() {
			return logDate;
		}

		public String getPathInfo() {
			return pathInfo;
		}
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
			productStatusDescription = object.optString("productStatusDescription");
			productInformation = object.optString("productInformation");
			String manufacturerDateString = object.optString("manufacturingDate");
			if (StringHelper.isStringNullOrEmpty(manufacturerDateString)) {
				manufacturingDate = StringHelper.convertStringToDate(manufacturerDateString);
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

		public List<KeyValuePair> getKeyValuePairsFromDetails() {
			if (StringHelper.isStringNullOrEmpty(details)) {
				return null;
			}

			List<KeyValuePair> pairs = new ArrayList<KeyValuePair>();

			String replacedDetails = details.replaceAll("△", "");
			String[] items = replacedDetails.split("\r\n");
			if (items != null && items.length > 0) {
				for (String item : items) {
					String[] keyValue = item.split(":");
					if (keyValue != null && keyValue.length == 2) {
						pairs.add(new KeyValuePair(keyValue[0], keyValue[1]));
					}
				}
			}
			return pairs;
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

		public List<ScanRecord> getScanRecordList() {
			if (latest == null || latest.length == 0) {
				return null;
			}
			List<ScanRecord> list = new ArrayList<ScanRecord>();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			for (String row : latest) {
				String[] columns = row.split("\\s");
				if (columns != null && columns.length > 2) {
					try {
						String dateString = columns[0] + " " + columns[1];
						Date date = format.parse(dateString);
						String scanInfo = row.replaceAll(dateString, "").trim();
						list.add(new ScanRecord(scanInfo, date));

					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			return list;

		}

	}

	public static class ScanRecord {
		private Date scanDate;
		private String scanInfo;

		public ScanRecord(String scanInfo, Date scanDate) {
			this.scanDate = scanDate;
			this.scanInfo = scanInfo;
		}

		public Date getScanDate() {
			return scanDate;
		}

		public String getScanInfo() {
			return scanInfo;
		}
	}

}
