/*
	Bootstrap树形插件使用到的方法
*/
	$("#tree").treeview({
		data:getTree(),
		showIcon: false,
		showCheckbox: true,
		showTags:true,
		levels:8,
		onNodeChecked: function(event,node){
			var selectedArr=[];
			if(ChildrenIsCheckedAll(node)&&!IsGrandParent(node)){
				ParentChecked(node);			//判断是否需要全选
			}
			ChildrenChecked(node); 				//如果为一个父节点则全选能选择的子节点
			if(!IsGrandParent(node)){			//找到这个模块的祖节点
				var grandParent=GetNode(FindGrandParent(node));
				selectedArr.push(GetChecked(grandParent,selectedArr));//获取该祖节点下所有被选中的子节点
				//ClearModule(grandParent.nodeId);
				if(!$("#list").find("#"+grandParent.nodeId).length)
					DrawModule(grandParent.text,selectedArr,grandParent.nodeId);
				else
					UpdateModuleInfo(grandParent.nodeId,selectedArr);
			}else{
				var childrenNodes=node.nodes;
				for(var i in childrenNodes){
					if(childrenNodes[i].nodes){
						var treeNode=GetNode(childrenNodes[i].nodeId);
						treeNode.state.expanded=false;
					}
				}
			}
		},
		onNodeUnchecked: function(event,node){
			var selectedArr=[];
			ChildrenUnchecked(node);
			if(!IsGrandParent(node)){
				var grandParent=GetNode(FindGrandParent(node));
				selectedArr.push(GetChecked(grandParent,selectedArr));//获取该祖节点下所有被选中的子节点
				if(selectedArr.length==1&&!selectedArr[0]){
					ClearModule(node.nodeId);
				}
				UpdateModuleInfo(grandParent.nodeId,selectedArr);
			}

		},
		onNodeSelected: function(event,node){
			var treeNode=GetNode(node.nodeId);
			
			
			if(node.nodes){
					treeNode.state.expanded=!treeNode.state.expanded;
			}else{
				if(treeNode.state.checked)
					$("#tree").treeview("uncheckNode",node.nodeId);
				else
					$("#tree").treeview("checkNode",node.nodeId);
			}
			
			$("#tree").treeview("unselectNode",node.nodeId);
		},
		onNodeUnselected: function(event,node){
			
			
		}
	});

	
	//页面载入时候遍历已选项并绘制
	function LoadTree(){
		var grand_parent_id=[];
		$(".node-tree").each(function(){
			grand_parent_id.push($(this).attr("data-nodeid"));
		})
		for(var i=0;i<grand_parent_id.length;i++){
			var grandParent=GetNode(grand_parent_id[i]);
			var selectedArr=[];
			selectedArr.push(GetChecked(grandParent,selectedArr));
			if(grandParent.state.checked){
				ChildrenChecked(grandParent);
			}else{
/*				for(var j=0;j<selectedArr.length-1;j++){
					if(ChildrenIsCheckedAll(GetNode(selectedArr[j].node_id)))
						ParentChecked(GetNode(selectedArr[j].node_id));
				}
				selectedArr=[];
				selectedArr.push(GetChecked(grandParent,selectedArr));
				ClearModule(grandParent.nodeId);*/
			
				DrawModule(grandParent.text,selectedArr,grandParent.nodeId);
			}		
		}

	}

	LoadTree();
	//清除之前所画的模块重新绘制
	function ClearModule(node_id){
		$("#list div#"+node_id).remove();
	}
	//在右侧画选中模块
	function DrawModule(title,content,node_id){
		var module_title=title;
		var arr=content;
		var items="";
		for(var i=0;i<arr.length;i++){
			if(arr[i]){
				if(arr[i].color)
					items+='<span style="color:'+arr[i].color+'" data="'+arr[i].id+'">'+arr[i].text+'</span>,';
				else
					items+='<span data="'+arr[i].id+'">'+arr[i].text+'</span>,';
			}
			
		}
		if(items!=""){
			items=items.substring(0,items.length-1);
			var mdHtml='<div class="selectedItem" id="'+node_id+'"><h2>'+module_title+'</h2><p class="content">'+items+'</p></div>';
			$("#list").append(mdHtml);
		}

	}
	//当页面Load之后修改所选项不再清除重绘而是在右边相应的地方更新
	function UpdateModuleInfo(node_id,content){
		var arr=content;
		var items="";
		for(var i=0;i<arr.length;i++){
			if(arr[i]){
				if(arr[i].color)
					items+='<span style="color:'+arr[i].color+'" data="'+arr[i].id+'">'+arr[i].text+'</span>,';
				else
					items+='<span data="'+arr[i].id+'">'+arr[i].text+'</span>,';
			}
		
		}
		if(items!=""){
			items=items.substring(0,items.length-1);
			$("#list div#"+node_id).find("p.content").html(items);
		}else{
			$("#list div#"+node_id).remove();
		}
		
		
	}

	//根据nodeId获取节点node对象
	function GetNode(node_id){
		return $("#tree").treeview('getNode',node_id);
	}

	//递归获取祖节点下被选中项
	 function GetChecked(node,arr){
		var obj={
				"text": node.text,
				"node_id":node.nodeId,
				"id":node.id,
				"parent_id":node.parentId,
				"color": node.color=="#dbdbdb"?"#333":node.color
			}		 
	 	if(node.nodes){
	 		if(node.state.checked){

	 			arr.push(obj);
	 		}	 	
	 		for(var i=0;i<node.nodes.length;i++){
	 			GetChecked(node.nodes[i],arr);
	 		}
	 	}else{
	 		if(node.state.checked){

	 			arr.push(obj);
	 		}

	 			
	 	}
	 }

	//判断是否为祖节点
	function IsGrandParent(node){
		var parentID=$("#tree").treeview('getParent',node.nodeId).nodeId;
		if(!parentID&&(parentID!=0)) return true;
		else return false;
	}

	//寻找祖节点
	function FindGrandParent(node){
		var i=0;
		var level=$("#tree").treeview('getNode',node.nodeId).lv;
		var parentID=node.nodeId;
		for(i=1;i<level;i++){
			parentID=$("#tree").treeview('getParent',parentID).nodeId;
		}
		return parentID;
	}

	//选中父节点
	function ParentChecked(node){
		/*if(!IsGrandParent(node))*/
			$("#tree").treeview('checkNode',node.parentId);
	}

	//是否全选子节点
	function ChildrenIsCheckedAll(node){
		var siblingsArr=$("#tree").treeview('getSiblings',node.nodeId);
		for(var i=0;i<siblingsArr.length;i++){
			if(!siblingsArr[i].state.checked)
				return false;
		}
		return true;
	}
	//取消父节点选中
	function CancelParentChecked(node){
		var level=$("#tree").treeview('getNode',node.nodeId).lv;
		var parentID=node.nodeId;
		for(var i=0;i<(level-1);i++){
			$("#tree").treeview('uncheckNode',parentID);
			parentID=$("#tree").treeview('getParent',parentID).nodeId;
		}


	}
	//取消子节点选中
	function ChildrenUnchecked(node){
		if(node.nodes){
			for(var i in node.nodes){
				var childNode=GetNode(node.nodes[i].nodeId);
				childNode.state.disabled=false;
				if(!childNode.special){
					childNode.color="#333";
					$("#tree").treeview("uncheckNode",[node.nodes[i].nodeId]);
				}
			}			
		}
	}
	//选中子节点，特殊节点不选中
	function ChildrenChecked(node){
		if(node.nodes){
			for(var i in node.nodes){
				if(!$("#tree").treeview("getNode",[node.nodes[i].nodeId]).special){
					var child=GetNode(node.nodes[i].nodeId);
					$("#tree").treeview("checkNode",[node.nodes[i].nodeId]);
					child.state.disabled=true;
					child.color="#dbdbdb";
					
				}
				if($("#tree").treeview("getNode",[node.nodes[i].nodeId]).special&&$("#tree").treeview("getNode",[node.nodes[i].nodeId]).state.checked){
					$("#tree").treeview("checkNode",[node.nodes[i].nodeId]);
				}
					
			}
		}
	}
	