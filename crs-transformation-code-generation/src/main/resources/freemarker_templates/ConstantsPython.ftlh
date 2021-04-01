'''
<#list rowsForClassLevelComment as rowForClassLevelComment>
| ${rowForClassLevelComment}
</#list>

| Tomas Johansson ( http://programmerare.com ) wrote the code that generated 
| this class with Python constants with EPSG codes for Coordinate Reference Systems.
| It is released with license MIT.
| Regarding the data itself: Ownership of the EPSG Dataset by IOGP is acknowledged.
| https://epsg.org/terms-of-use.html
| Some quotes from the above 'Terms of use' URL:
| "   The EPSG Facilities are published by IOGP at no charge. 
|     Distribution ... for profit is forbidden ... 
|     Ownership of the EPSG Dataset by IOGP must be acknowledged... "
'''
class ${nameOfClass}:
    <#t>
    <#list constants as constant>

    ${constant.nameForConstant} = ${constant.valueForConstant?no_esc}
    """
    | ${constant.documentationCommentForConstant}
    | https://epsg.io/${constant.epsgNumber?c}
    | http://spatialreference.org/ref/epsg/${constant.epsgNumber?c}/
    """    
    </#list>
