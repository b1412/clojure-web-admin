google.charts.packageLoadedCallback("orgchart", 'var o3a=" google-visualization-orgchart-linebottom",p3a=" google-visualization-orgchart-lineleft",q3a=" google-visualization-orgchart-lineright",r3a=" google-visualization-orgchart-node-",s3a="google-visualization-orgchart-connrow-",t3a="google-visualization-orgchart-linenode",u3a="google-visualization-orgchart-node",v3a="google-visualization-orgchart-noderow-",w3a="google-visualization-orgchart-nodesel",x3a="google-visualization-orgchart-space-",y3a="google-visualization-orgchart-table",z3a="large",\nA3a="selectedStyle",B3a="small";function pZ(a){this.L=a;this.m={};this.G=null;this.F=Ik();this.Dc=new pt;this.Eh=null}Lk("/orgchart/orgchart.css");pZ.prototype.B3=0;pZ.prototype.draw=function(a,b){this.m=b=b||{};this.G=a;if(!this.L)throw Error(eb);if(!a)throw Error(Rv);var c=new tT(a,{Xaa:!1,Yaa:!1,yba:!1});this.Eh=new uT(c,function(a){return new C3a(a)});this.nE(this.Eh,b);ds(this,Gp,{})};\nfunction C3a(a){qT.call(this,a.getId(),a.getName());this.row=a.getId();this.Pqa=IRa(a);this.style=a.Rs(de);this.Jya=a.Rs(A3a);this.label=3==a.Ya().S()?a.Oa(2):null;this.G6=this.x=null;this.collapsed=!1}O(C3a,qT);function D3a(a,b){var c=[];a.Eh.aN(function(a,e){e==b&&c.push(a);return!a.collapsed&&e<b},a);eg(c,function(a,b){return a.row-b.row});return c}function E3a(a,b){var c=b.ig(),d=c.length;if(0==d)b.x=a.B3++;else{for(var e=0;e<d;e++)E3a(a,c[e]);b.x=(c[0].x+c[d-1].x)/2}}I=pZ.prototype;\nI.nE=function(a,b){var c=this.L;this.B3=0;for(var d=D3a(this,0),e=0;e<d.length;e++)E3a(this,d[e]);d=b.size;d!=z3a&&d!=B3a&&(d=yd);var f=this.F,g=f.B(Qp,{"class":y3a,dir:dB,cellpadding:Ha,cellspacing:Ha,align:Qx}),h=f.B(iE);f.appendChild(g,h);var l=8*this.B3-2,m=f.B(CE,null);f.appendChild(h,m);for(var n=0;n<l;n++){var p=f.B(jE,{"class":x3a+d});f.appendChild(m,p)}m=a.getHeight()+1;for(n=0;n<m;n++){var q=D3a(this,n),t,u;if(0<n){t=[];for(var v=0;v<q.length;v++)u=q[v],p=u.getParent(),e=Math.round(8*u.x+\n3),p.x>=u.x?((p=t[e])||(p=t[e]={}),p.borderLeft=!0):((p=t[--e])||(p=t[e]={}),p.borderRight=!0);qZ(this,t,l,h,s3a+d,d,b)}t=[];for(v=0;v<q.length;v++)u=q[v],e=Math.round(8*u.x),(p=t[e])||(p=t[e]={}),p.node=u,p.span=6;qZ(this,t,l,h,v3a+d,d,b);if(n!=m){t=[];for(v=0;v<q.length;v++){u=q[v];var w=u.ig();if(0<w.length&&(e=Math.round(8*u.x+3),(p=t[e])||(p=t[e]={}),p.borderLeft=!0,!u.collapsed))for(u=Math.round(8*w[w.length-1].x+3),e=Math.round(8*w[0].x+3);e<u;e++)(p=t[e])||(p=t[e]={}),p.borderBottom=!0}qZ(this,\nt,l,h,s3a+d,d,b)}}f.bc(c);f.appendChild(c,g)};\nfunction qZ(a,b,c,d,e,f,g){var h=g.nodeClass||u3a,l=a.F;e=l.B(CE,{"class":e});l.appendChild(d,e);for(d=0;d<c;d++){var m=b[d],n=l.B(jE,null);if(!m){for(var m={empty:!0},p=d+1;p<c&&!b[p];)p++;m.span=p-d}(p=m.span)&&1<p&&(n.colSpan=p,d+=p-1);p=k;if(m.node){m.node.G6=n;var p=h+r3a+f,q=m.node.row;null!=q&&(Kq(n,Ap,N(a.b1,a,q)),Kq(n,Cp,N(a.d1,a,q)),Kq(n,Bp,N(a.c1,a,q)),a.m.allowCollapse&&Kq(n,Ny,N(a.ysa,a,q)))}else p=t3a,m.borderLeft&&(p+=p3a),m.borderRight&&(p+=q3a),m.borderBottom&&(p+=o3a);p&&(n.className=\np,-1<p.indexOf(h)&&(g.color&&(n.style.background=g.color),p=m.node&&m.node.style))&&(n.style.cssText=p);p=m.node?m.node.Pqa:De;m=m.node?m.node.label:null;null!=m&&(n.title=m);g.allowHtml?n.innerHTML=p:l.appendChild(n,l.createTextNode(p));l.appendChild(e,n)}}I.getSelection=function(){return this.Dc.getSelection()};\nI.setSelection=function(a){var b=this.m,c=this.Dc.setSelection(a);if(this.L){a=b.selectedNodeClass||w3a;for(var d=rt(c.lE),e=0;e<d.length;e++){var f=d[e],g=0<=f?this.Eh.Vv[f]||null:null;g&&(f=g.G6)&&(DH(f,a),b.color&&(f.style.background=b.color),g=g.style)&&(f.style.cssText=g)}c=rt(c.OA);for(e=0;e<c.length;e++)if(f=c[e],g=0<=f?this.Eh.Vv[f]||null:null)if(f=g.G6)if(BH(f,a),b.selectionColor&&(f.style.background=b.selectionColor),d=g.Jya)f.style.cssText=d}};\nI.b1=function(a){a=tt(this.Dc,a)?null:[{row:a}];this.setSelection(a);ds(this,Mp,{})};I.d1=function(a){ds(this,mC,{row:a})};I.c1=function(a){ds(this,lC,{row:a})};I.ysa=function(a){var b=this.Eh.Vv[a]||null;this.collapse(a,!(b&&b.collapsed))};I.bra=function(){var a=this.Eh.find(function(a){return a.collapsed});return Q(a,function(a){return a.row})};I.ara=function(a){a=this.Eh.Vv[a]||null;if(!a)return[];a=a.ig();for(var b=[],c=0;c<a.length;c++)b.push(a[c].row);return b};\nI.collapse=function(a,b){var c=this.Eh.Vv[a]||null;c&&c.ig()&&0!=c.ig().length&&(b&&!c.collapsed||!b&&c.collapsed)&&(c.collapsed=b,this.F.bc(this.L),this.nE(this.Eh,this.m),ds(this,Gp,{}),ds(this,Rta,{collapsed:b,row:a}))};K("google.visualization.OrgChart",pZ,void 0);pZ.prototype.draw=pZ.prototype.draw;pZ.prototype.getSelection=pZ.prototype.getSelection;pZ.prototype.setSelection=pZ.prototype.setSelection;pZ.prototype.getChildrenIndexes=pZ.prototype.ara;pZ.prototype.getCollapsedNodes=pZ.prototype.bra;pZ.prototype.collapse=pZ.prototype.collapse;;window.google&&window.google.loader&&window.google.loader.eval&&window.google.loader.eval.visualization&&(window.google.loader.eval.visualization=function(){eval(arguments[0])});\n');