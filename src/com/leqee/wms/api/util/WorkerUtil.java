package com.leqee.wms.api.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.Column;

import org.apache.log4j.Logger;

/**
 *
 * @author chenl
 *
 */
public class WorkerUtil {
    private WorkerUtil() {

    }
    
    private static final SimpleDateFormat datetimeFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    
    /**
     * 时间转换成字符串，格式：yyyy-MM-dd HH:mm:ss
     * 
     * @param date
     * @return
     */
    public static String formatDatetime(Date date) {
        return datetimeFormat.format(date);
    }
    
    /**
     * 字符串转换成时间，格式：yyyy-MM-dd HH:mm:ss
     * 
     * @param source
     * @return
     * @throws ParseException
     */
    public static Date parseDatetime(String source) throws ParseException {
        return datetimeFormat.parse(source);
    }
    
    
    /**
     * 获取历元年日期：yyyy-MM-dd HH:mm:ss
     * @param source
     * @return
     */
    public static Date getEpochDate()  {
    	Date date = new Date( 0 ) ;
        try {
        	date = datetimeFormat.parse("1970-01-01 00:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return date;
    }
    

    /**
    * 根据传入字符串格式的时间，返回日期格式
    * @param time
    * @return
    */
    public static Timestamp string2Timestamp(String time) {
        if (time == null) {
            return null;
        }

        if (!time.contains(":")) {
            time = time.trim().concat(" 00:00:00");
        }

        return Timestamp.valueOf(time);
    }

    /**
     * 根据传入的字符串格式的数字，返回数字格式
     * @param number
     * @return
     */
    public static BigDecimal string2BigDecimal(String number) {
        if (number == null) {
            return null;
        }

        return new BigDecimal(number);
    }

    /**
     * 根据传入的字符串格式的数字，返回数字格式
     * @param number
     * @return
     */
    public static BigInteger string2BigInteger(String integer) {
        if (integer == null) {
            return null;
        }

        return new BigInteger(integer);
    }

    /**
     * 用于返回查询的对象的String形式
     * 如果为空则返""
     * @param obj
     * @return
     */
    public static String getStringValue(Object obj) {
        if (obj != null) {
            return obj.toString();
        } else {
            return "";
        }
    }

    /**
     * 用于返回查询的对象的String形式
     * 如果为空则返默认值
     * @param obj
     * @param defaultValue 默认值
     * @return
     */
    public static String getStringValue(Object obj, String defaultValue) {
        if (obj != null && !obj.equals("")) {
            return obj.toString();
        } else {
            return defaultValue;
        }
    }

    /**
     * 返回对象的数字形式，一般用于带小数点的数字
     * 如果是空则将数字置0
     * @param obj
     * @return
     */
    public static BigDecimal getDecimalValue(Object obj) {
        if (obj != null && !obj.equals("")) {
            return new BigDecimal(obj.toString());
        } else {
            return new BigDecimal("0");
        }
    }

    /**
     * 返回对象的数字形式，一般用于带小数点的数字,并设置保留位数
     * 如果是空则将数字置0
     * @param obj
     * @param scale
     * @return
     */
    public static BigDecimal getDecimalValue(Object obj, int scale) {
        if (obj != null) {
            return new BigDecimal(obj.toString()).setScale(scale, BigDecimal.ROUND_HALF_UP);
        } else {
            return new BigDecimal("0").setScale(scale, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * 返回对象的数字形式，只能用于整数
     * 如果是空则将数字置0
     * @param obj
     * @return
     */
    public static BigInteger getIntegerValue(Object obj) {
        if (obj != null) {
            return new BigInteger(obj.toString());
        } else {
            return new BigInteger("0");
        }
    }

    /**
     * 返回对象的日期格式
     * @param obj
     * @return
     */
    public static Timestamp getTimestampValue(Object obj) {
        if (obj == null) {
            return null;
        }

        return string2Timestamp(obj.toString());
    }

    /**
     * 判断对象是否为空，为空则报错
     * @param obj 对象
     * @param message 报错信息
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void checkNullOrEmpty(Object obj, String message) {
        if (obj == null) {
            throw new RuntimeException(message);
        }
        if (obj instanceof String) {
            if (obj.toString().equals("")) {
                throw new RuntimeException(getMessage(message));
            }
        }
        if (obj instanceof Collection) {
            if (((Collection) obj).size() == 0) {
                throw new RuntimeException(getMessage(message));
            }
        }
    }

    /**
     * 判断对象是否为空，返回true | false
     * @param obj 对象
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isNullOrEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            if (obj.toString().equals("")) {
                return true;
            }
        }
        if (obj instanceof Collection) {
            if (((Collection) obj).size() == 0) {
                return true;
            }
        }
        if (obj instanceof Map) {
            if (((Map) obj).size() == 0) {
                return true;
            }
        }
        if (obj instanceof StringBuffer) {
            if (((StringBuffer) obj).length() == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * 对于同一个对象用Or逻辑判断是否与其他对象相等
     * @param obj 指定对象
     * @param options 需要判断的对象
     * @return
     * @throws Exception
     */
    public static boolean checkOrEquals(Object obj, Object... options) {
        if (obj == null) {
            return false;
        }
        if (options.length == 0) {
            return false;
        }
        for (Object o : options) {
            if (obj.equals(o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 加入错误娱乐信息
     * @param message
     * @return
     */
    public static String getMessage(String message) {
        String[] info = { "HOHO！", "~\\(≧▽≦)/~", "我靠！", "刮风啦下雨啦大家收衣服啊！", "这是为什末呢？", "输给你了！",
                "想作弊？", "楼主快回火星吧！", "哥们儿三思啊！", "乖~再试一次！", "呼~差一点~", "你中奖了！", "异想天开？", "很遗憾~",
                "我不玩了！", "人是人他妈生的，妖是妖他妈生的……", "再错请我吃饭！", "I 服了 U", "你厉害！", "去，面壁思过5分钟！", "……无语",
                "我不信是你按错了……", "O(∩_∩)O", "何必呢？", "晕……", "何苦呢？", "有意思吗？", "今天天气不错啊~", "(*^__^*)",
                "看你气色不太好，病了？", "( ⊙ o ⊙ )", "去洗把脸吧！清醒一下！", "╮(╯﹏╰）╭", "我很同情你！", "(*^◎^*)",
                "我也是被逼的！", "(=@__@=)", "你要冷静！", "在哪跌倒，就在哪躺下！", "别乱来，我要叫啦！", "搞什么飞机？",
                "Oh, my God!" };
        if (message != null) {
            int i = new Random().nextInt(info.length);
            return message.concat(info[i]);
        } else {
            return null;
        }
    }

    /**
     * 返回变量如果是空的报错信息
     * @param varName 变量的中文含义
     * @return
     */
    public static String getNullMessage(String varName) {
        if (varName == null || varName.equals("")) {
            return varName;
        } else {
            return getMessage(varName.concat("不能为空！"));
        }
    }

    /**
     * 记录错误信息，并且扔出运行时错误
     * @param log  logger变量
     * @param e    错误
     * @param message  记录错误信息
     */
    public static void log(Logger log, Throwable e, String message) {
        log.error(message, e);
        throw new RuntimeException(e.getMessage());
    }

    /**
     * 快捷扔出运行时错误
     * @param message 错误信息
     */
    public static void throwRuntimeException(String message) {
        throw new RuntimeException(getMessage(message));
    }

    /**
     * 根据class获得该class注解为column的method
     * @param cls 类
     * @return    对应的Map
     */
    public static Map<String, Method> getColumnMethod(Class cls) {
        Method[] methods = cls.getMethods();
        Map<String, Method> result = new HashMap<String, Method>();
        for (Method method : methods) {
            Column column = method.getAnnotation(Column.class);
            if (column != null) {
                String name = column.name();
                if (name != null && !"".equals(name))
                    result.put(name, method);
            }
        }

        return result;
    }

/**
     * 按二进制拆分一个整数
     *
     * @param value
     * @return
     */
    public static List<Integer> getIntegerByte(Integer value) {
        List<Integer> result = new ArrayList<Integer>();
        int base = 1;
        int mod = 0;
        while (base <= value) {
            // 是否存在在某位上相等
            mod = value & base;
            if (mod != 0) {
                result.add(base);
            }
            // base 乘 2
            base <<= 1;
        }
        return result;
    }

    /**
     * 获得当前timestamp
     * @return
     */
    public static Timestamp getNow() {
        Date today = new java.util.Date();
        return new Timestamp(today.getTime());
        
    }
    
    
    public static String collection2String(Collection<?> collection) {
        if (isNullOrEmpty(collection)) {
            return null;
        }
        
        String result = "";
        Iterator<?> e = collection.iterator();
        while (e.hasNext()) {
            if (!isNullOrEmpty(result)) {
                result += " , ";
            }
            
            result += e.next().toString();
        }
        
        return result;
    }
    
    /**
     * 将data中javaBean的数据传入到target中
     * 
     * @param data 拥有数据，需要提取
     * @param target 被注入数据的类
     * */
//    public static void insertObject(Class<?> _class, Class<?> targetClass,String dataObjName, String targetObjName){
//    	//提取拥有数据的类的所有方法
//    	Method[] methods =_class.getMethods();
//    	//提取目标类的所有方法
//    	Method[] targetMethods = targetClass.getMethods();
//    	Map<String, Method> map = new HashMap<String, Method>();
//    	//提取目标类的所有方法，将其放入map集合中
//    	for(Method method : targetMethods){
//    		map.put(method.getName(), method);
//    	}
//    	for(Method method : methods){
//    		String name = method.getName();
//    		String targetName = ""; 
//    		//判断原方法中有没有get开头的方法，如果有则判断目标类中有没有对应的set方法
//    		
//    		if(name.startsWith("get")){
//    			String s = name.substring(3);
//    			targetName = s.replaceAll("([A-Z])+", "_"+("$1")).toLowerCase();
//    			targetName = "set" + targetName.replaceFirst(targetName.substring(0,2),targetName.substring(1,2).toUpperCase());
//    		}
//    		if("".equals(targetName)) continue;
//    		//如果有对应的set方法则通过map取出来
//    		Method targetMethod = map.get(targetName);
//    		if( targetMethod == null) continue;
//    		//System.out.println(targetObjName+"."+targetName+"("+dataObjName+"."+name+"());");
////    		try {
////    			//执行拥有数据类的get方法来获取数据obj
////    			Object obj = method.invoke(data);
////    			if(!targetMethod.getParameterTypes()[0].equals(obj.getClass())) continue;
////    			//执行目标类的set方法将数据obj放入目标对象中
////    			targetMethod.invoke(target, obj);
////			} catch (Exception e) {
////				continue;
////			}
//    		
//    	}
//    }
    
    public static void main(String[] args){
    
    }
    
    /**
     * 根据传入的class对象的属性获取对应的插入sql，不过结果需要一些修正
     * */
    public static String getSqlString(Class<?> _class){
    	//获取所有私有属性
    	Field[] fields = _class.getDeclaredFields();
    	//Db字符串，即拥有下划线的字段
    	StringBuffer resultDb = new StringBuffer("(");
    	//value字段，即拥有大写的字段
    	StringBuffer result = new StringBuffer("(");
    	//update字段，更新操作需要
    	StringBuffer update = new StringBuffer("");
    	Field.setAccessible(fields, true);
    	for(Field field : fields){
    		//获取属性名称，即大写
    		String name = field.getName();
    		//将所有大写对应位置转化为_小写，即数据库对应字段
    		String dbName = name.replaceAll("([A-Z])+", "_"+("$1")).toLowerCase();
    		//添加，mybatis专用
    		result.append(", #{"+ name +"}");
    		resultDb.append(", "+ dbName);
    		update.append(", " + dbName + "= #{" + name + "}");
    	}
    	//拼接结果
    	result = new StringBuffer(result.toString().replaceFirst(",", ""));
    	resultDb = new StringBuffer(resultDb.toString().replaceFirst(",", ""));
    	update = new StringBuffer(update.toString().replaceFirst(",", ""));
    	resultDb.append(") VALUES \n").append(result).append(" ) ON DUPLICATE KEY UPDATE  \n")
    	.append(update);
    	//返回结果
    	return resultDb.toString();
    }
    
    /**
     * 将一个list以num个元素为一个list进行划分，获取一个关于list集合的集合
     * 
     * @param list 原集合
     * @param num 每num个元素自成一个list
     * */
    public static <T> List<List<T>>  seprateList(List<T> list, int num){
    	
    	List<List<T>> lists = new ArrayList<List<T>>();
    	
    	if(num == 0 || list == null){
    		return lists;
    	}
    	int size = list.size();
    	int count = size / num;
    	int lastNum = size % num;
    	
    	for(int i = 0; i < count ; i++){
    		lists.add(list.subList(i * num, (i+1) * num));
    	}
    	if(lastNum != 0){
    		lists.add(list.subList( size-lastNum , size));
    	}
    	
    	return lists;
    }
    
    public static <T> String Collection2StringSign(Collection<T> collection, char sign){
    	if(isNullOrEmpty(collection)){
    		return "";
    	}
    	StringBuffer result = new StringBuffer();
    	for(T obj : collection){
    		result.append(sign + obj.toString());
    	}
    	return result.toString().replaceFirst(Character.toString(sign), "");
    }

    
    
    /**
	 * 验证参数是否为空，一旦有空就返回false
	 * @param parameters  需要验证的参数
	 * @return
	 */
	public static boolean hasNoEmpty( Object ...parameters ) {
		
		if(WorkerUtil.isNullOrEmpty(parameters)){
			return false;
		}
		
		for( Object param : parameters ){
			if( WorkerUtil.isNullOrEmpty(param)){
				return false;
			}
			
		}
		
		return true;
	}
    
   
}
