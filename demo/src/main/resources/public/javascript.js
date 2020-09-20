const width = 300;
const height = 2500;
const margin = 15;
const padding = 15;
const adj = 30;
//we are appending SVG first
const svg = d3.select("body").append("svg").attr("width", width + adj)
.attr("height", height + adj).append("g").attr("transform",
		"translate(" + adj + "," + margin + ")");

//we are appending SVG second
const svg2 = d3.select("body").append("svg").attr("width", width + adj)
.attr("height", height + adj).append("g").attr("transform",
		"translate(" + adj + "," + margin + ")");

//we are appending SVG second
const svg3 = d3.select("body").append("svg").attr("width", width + adj)
.attr("height", height + adj).append("g").attr("transform",
		"translate(" + adj + "," + margin + ")");

//we are appending SVG second
const svg4 = d3.select("body").append("svg").attr("width", width + adj)
.attr("height", height + adj).append("g").attr("transform",
		"translate(" + adj + "," + margin + ")");

//-----------------------------DATA------------------------------//

const dataset = d3.csv("http://localhost:8080/getAZIM");
dataset
.then(function(data) {
	const slices = data.columns.slice(1).map(function(id) {
		return {
			id : id,
			values : data.map(function(d) {
				return {
					dept : d.DEPT,
					measurement : +d[id]
				};
			})
		};
	});

	data.forEach(function(d) {
		d.DEPT = +d.DEPT;
	});

	//console.log("slicesMM",slicesMM);

	// List of groups = header of the csv files
	var keys = data.columns.slice(1);

	//console.log("Column headers", data.columns);
	//console.log("Column headers without date", data.columns.slice(0));
	// returns the sliced dataset
	console.log("Slices", slices);
	// returns the first slice
	//console.log("First slice", slices[0]);
	// returns the array in the first slice
	//console.log("A array", slices[0].values);
	// returns the date of the first row in the first slice
	//console.log("Date element", slices[0].values[0].dept);
	// returns the array's length
	//console.log("Array length", (slices[0].values).length);

	//----------------------------SCALES-----------------------------//
	const xScale = d3.scaleLinear().rangeRound([ 0, width ]);
	const yScale = d3.scaleLinear().range([ 0, height ]);

	xScale.domain([ (0), d3.max(slices, function(c) {
		return d3.max(c.values, function(d) {
			return d.measurement;
		});
	}) ]);

	var maxDept = d3.min(data, function(d) {
		return d.DEPT;
	});

	yScale.domain([ 0, maxDept ]);

	//-----------------------------AXES------------------------------//
	const yaxis = d3.axisLeft().scale(yScale).ticks(40);
	const xaxis = d3.axisTop().scale(xScale);

	//----------------------------LINES------------------------------//
	const line = d3.line().curve(d3.curveBasis).x(function(d) {
		return xScale(d.measurement);
	}).y(function(d) {
		return yScale(d.dept);
	});

	let id = 0;
	const ids = function() {
		return id++;
	}

	//-------------------------2. DRAWING----------------------------//

	//-----------------------------AXES LINES------------------------------//
	svg.append("g").attr("class", "axis").attr("transform",
			"translate(0," + height + ")").call(xaxis);

	svg.append("g").attr("class", "axis").call(yaxis);

	svg.append("g").attr("class", "axis").call(xaxis);
	//----------------------------LINES------------------------------//
	var color = d3.scaleOrdinal(d3.schemeCategory10); // set the colour scale
	console.log("color", color(ids));

	const lines = svg.selectAll("lines").data(slices).enter()
	.append("g");

	lines.append("path").attr("class", "line").style("stroke",
			function(d, i) {
		return color(i);
	}) // assign ID
	.attr("d", function(d) {
		return line(d.values);
	});

	var mouseG = svg.append("g") // this the black vertical line to folow mouse
	.attr("class", "mouse-over-effects");

	mouseG.append("path").attr("class", "mouse-line").style(
			"stroke", "black").style("stroke-width", "1px")
			.style("opacity", "0");

	var mousePerLine = mouseG.selectAll(".mouse-per-line")
	.data(slices).enter().append("g").attr("class",
	"mouse-per-line");

	mousePerLine.append("circle").attr("r", 7).style("stroke",
			function(d) {
		return color(d.value);
	}).style("fill", "none").style("stroke-width",
	"1px").style("opacity", "0");

	mousePerLine.append("text").attr("transform",
	"translate(10,3)");

	mouseG.append("rect")
	.attr("width", width)
	.attr("height", height)
	.attr("fill", "none")
	.attr("pointer-events", "all")
	.on("mouseout",
			function() {
		d3.select(".mouse-line").style(
				"opacity", "0");
		d3.selectAll(".mouse-per-line circle")
		.style("opacity", "0");
		d3.selectAll(".mouse-per-line text")
		.style("opacity", "0")
	})
	.on(
			"mouseover",
			function() {
				d3.select(".mouse-line").style(
						"opacity", "1");
				d3.selectAll(".mouse-per-line circle")
				.style("opacity", "1");
				d3.selectAll(".mouse-per-line text")
				.style("opacity", "1")
			})
			.on(
					"mousemove",
					function() {

						var mouse = d3.mouse(this);
						console.log("Mouse:", mouse);
						d3.select(".mouse-line").attr(
								"d",
								function() {
									var d = "M" + mouse[0]
									+ "," + h;
									d += " " + mouse[0] + ","
									+ 0;
									return d;
								})
								// .attr("d",function(){
								//   var d = "M" + w +"," + mouse[1];
								//   d+=" " +0 + "," + mouse[1];
								//   return d;
								// });

								d3
								.selectAll(".mouse-per-line")
								.attr(
										"transform",
										function(d, i) {
											console
											.log(w
													/ (mouse[0]));
											var xDate = scaleX
											.invert(mouse[0]), bisect = d3
											.bisector(function(
													d) {
												return d.date;
											}).right;
											idx = bisect(
													d.values,
													xDate);
											console.log(
													"xDate:",
													xDate);
											console.log(
													"bisect",
													bisect);
											console.log("idx:",
													idx)

													var beginning = 0, end = lines[i]
											.getTotalLength(), target = null;

											console.log("end",
													end);

											while (true) {
												target = Math
												.floor((beginning + end) / 2)
												console
												.log(
														"Target:",
														target);
												pos = lines[i]
												.getPointAtLength(target);
												console
												.log(
														"Position",
														pos.y);
												console
												.log(
														"What is the position here:",
														pos)
														if ((target === end || target == beginning)
																&& pos.x !== mouse[0]) {
															break;
														}

												if (pos.x > mouse[0])
													end = target;
												else if (pos.x < mouse[0])
													beginning = target;
												else
													break; // position found
											}
											d3
											.select(
													this)
													.select(
													"text")
													.text(
															scaleY
															.invert(
																	pos.y)
																	.toFixed(
																			1))
																			.attr(
																					"fill",
																					function(
																							d) {
																						return color(d.name)
																					});
											return "translate("
											+ mouse[0]
											+ ","
											+ pos.y
											+ ")";

										});

					});

	//-----------------------------AXES AREA------------------------------//
	svg2.append("g").attr("class", "axis").attr("transform",
			"translate(0," + height + ")").call(xaxis);

	svg2.append("g").attr("class", "axis").call(yaxis);

	svg2.append("g").attr("class", "axis").call(xaxis);

	//-----------------------------AREA------------------------------//

	g2 = svg2.append("g");

	// Area generator
	var area = d3.area().y(function(d) {
		//console.log("Areeeee",d.DEPT);
		return yScale(d.dept);
	}).x0(function(d) {
		return xScale(0);
	}).x1(function(d) {
		return xScale(d.measurement);
	});

	var source = g2.selectAll(".area").data(slices).enter()
	.append("g").attr("class", function(d) {
		return `area ${d.id}`;
	})

	source.append("path").attr("d", function(d) {
		return area(d.values);
	}).style("fill", function(d, i) {
		return color(i);
	}).style("stroke", "black").style("stroke-width", 0.5);

	//-----------------------------AXES DIFFERENCE AREA------------------------------//
	svg3.append("g").attr("class", "axis").attr("transform",
			"translate(0," + height + ")").call(xaxis);

	svg3.append("g").attr("class", "axis").call(yaxis);

	svg3.append("g").attr("class", "axis").call(xaxis);

	//-----------------------------DIFFERENCE AREA------------------------------//
	svg3.datum(data);
	var maxWidth = 900;

	svg3.append("clipPath").attr("id", "clip-above").append(
	"path").attr("d", d3.area().y(function(d) {
		return yScale(d.DEPT);
	}).x0(function(d) {
		return xScale(0);
	}).x1(function(d) {
		return xScale(d.ILM);
	}));

	svg3.append("clipPath").attr("id", "clip-below").append(
	"path").attr("d", d3.area().y(function(d) {
		return yScale(d.DEPT);
	}).x0(function(d) {
		return xScale(maxWidth);
	}).x1(function(d) {
		return xScale(d.ILM);
	}));

	svg3.append("path").attr("clip-path", "url(#clip-above)")
	.attr("fill", color(2)).attr("d",
			d3.area().y(function(d) {
				return yScale(d.DEPT);
			}).x0(function(d) {
				return xScale(maxWidth);
			}).x1(function(d) {
				return xScale(d.ILD);
			}));

	svg3.append("path").attr("clip-path", "url(#clip-below)")
	.attr("fill", color(1)).attr("d",
			d3.area().y(function(d) {
				return yScale(d.DEPT);
			}).x0(function(d) {
				return xScale(0);
			}).x1(function(d) {
				return xScale(d.ILD);
			}));

	//console.log("Data", data);

	//-----------------------------AXES 3D------------------------------//
	svg4.append("g").attr("class", "axis").attr("transform",
			"translate(0," + height + ")").call(xaxis);

	svg4.append("g").attr("class", "axis").call(yaxis);

	svg4.append("g").attr("class", "axis").call(xaxis);

});