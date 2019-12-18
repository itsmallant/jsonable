package com.pt.jsonable.bean

import com.pt.jsonable.annotation.JSONAble
import com.pt.thirdlib.Pit
import java.util.ArrayList

/**
 *@desc:
 *@author: ningqiang.zhao
 *@time: 2019-12-18 10:40
 **/
@JSONAble
class Jick(val name:String? = null,
           var pits: Collection<Pit>? = null)