package com.yuji.contentcore.perm;

import com.yuji.common.core.utils.StringUtils;
import com.yuji.contentcore.utils.CmsPrivUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 页面部件权限类型
 * 
 * @author Liguoqiang
 */
@Component(IPermissionType.BEAN_PREFIX + PageWidgetPermissionType.ID)
public class PageWidgetPermissionType implements IPermissionType {

	public static final String ID = "PageWidget";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "页面部件权限";
	}

	/**
	 * {<pageWidgetId: [long]>,...}
	 */

	@Override
	public String serialize(Set<String> permissionKeys) {
		Map<String, BitSet> map = new HashMap<>();
		permissionKeys.forEach(key -> {
			String[] arr = key.split(Spliter);
			if (arr.length == 3) {
				map.computeIfAbsent(arr[2], siteId -> new BitSet())
						.set(PageWidgetPrivItem.valueOf(arr[1]).bitIndex());
			}
		});
		return CmsPrivUtils.serializeBitSetPermission(map);
	}

	/**
	 * {<catalogId: [long]>,...}
	 */
	@Override
	public Set<String> deserialize(String json) {
		Set<String> privs = new HashSet<>();
		if (StringUtils.isNotEmpty(json)) {
			Map<String, BitSet> map = CmsPrivUtils.deserializeBitSetPermission(json);
			map.forEach((k, v) -> {
				if (!v.isEmpty()) {
					Long siteId = Long.valueOf(k);
					for (PageWidgetPrivItem privItem : PageWidgetPrivItem.values()) {
						if (v.get(privItem.bitIndex())) {
							privs.add(privItem.getPermissionKey(siteId));
						}
					}
				}
			});
		}
		return privs;
	}

	@Override
	public boolean hasPermission(List<String> permissionKeys, String json) {
		Map<String,BitSet> bitSetMap = CmsPrivUtils.deserializeBitSetPermission(json);
		//if (mode == SaMode.AND) {
			for (String key : permissionKeys) {
				String[] split = StringUtils.split(key, Spliter);
				BitSet bitSet = bitSetMap.get(split[2]);
				if (bitSet == null || !bitSet.get(PageWidgetPrivItem.valueOf(split[1]).bitIndex())) {
					return false;
				}
			}
			return true;
		/*} else {
			for (String key : permissionKeys) {
				String[] split = StringUtils.split(key, Spliter);
				BitSet bitSet = bitSetMap.get(split[2]);
				if (bitSet != null && bitSet.get(PageWidgetPrivItem.valueOf(split[1]).bitIndex())) {
					return true;
				}
			}
			return false;
		}*/
	}

	/**
	 * 页面部件权限项
	 */
	public enum PageWidgetPrivItem implements BitSetPrivItem {

		View(0, "查看"),

		Edit(1, "编辑"),

		Delete(2, "删除"),

		Publish(3, "发布");

		/**
		 * 权限项在bitset中的位置序号，从0开始，不可随意变更，变更后会导致原权限信息错误
		 */
		private final int bitIndex;

		private final String label;

		PageWidgetPrivItem(int bitIndex, String label) {
			this.bitIndex = bitIndex;
			this.label = label;
		}

		@Override
		public int bitIndex() {
			return this.bitIndex;
		}

		public String label() {
			return this.label;
		}

		public String getPermissionKey(Long pageWidgetId) {
			return ID + Spliter + this.name() + Spliter + pageWidgetId;
		}
	}
}
