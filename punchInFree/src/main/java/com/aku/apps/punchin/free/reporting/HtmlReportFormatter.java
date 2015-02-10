package com.aku.apps.punchin.free.reporting;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.aku.apps.punchin.free.R;
import com.aku.apps.punchin.free.domain.File;
import com.aku.apps.punchin.free.domain.RequiresContext;
import com.aku.apps.punchin.free.utils.Constants;
import com.aku.apps.punchin.free.utils.FileUtil;

/**
 * Formats reports into html.
 * @author Peter Akuhata
 *
 */
public class HtmlReportFormatter implements ReportFormatter, RequiresContext {
	private Context context = null;
	
	@Override
	public File format(Report report) {
		Log.d(ReportManager.class.getSimpleName(), "format(report=" + report.getName() + ")");

		String template = FileUtil.readRawTextFile(context, R.raw.html_template);
		ArrayList<String> columns = report.getColumns();
		
		String data = "<table>\r\n<tbody>";
		int count = 0;
		
		for (Row row : report.getRows()) {
			if (row.isHeader()) {
				data += "\r\n<tr class='" + row.getCssClass() + "'><td colspan='" + report.getColumns().size() + "'>" + row.getHeader() + "</td></tr>";
				
				data += "<tr class='columns'>";
				
				for (String item : columns)
					data += "<td>" + item + "</td>";
				
				data += "</tr>";
				
				count = 0;
				
			} else {
				data += "\r\n<tr class='" + (count % 2 == 1 ? "alternate " : "") + row.getCssClass() + "'>";
				count++;
				
				for (String item : row.getData())
					data += "<td>" + item + "</td>";
				
			}
			
			data += "</tr>\r\n";
		}
		
		data += "\r\n</tbody>\r\n</table>";
		data = template.replace("{body}", data);
		data = data.replace("{title}", report.getName());
		
		File file = new File(FileUtil.formatForFilename(report.getName()) + ".html", data, "text/html");		
		return file;
	}

	@Override
	public int getId() {
		return Constants.ReportFormatters.HTML;
	}

	@Override
	public int getName() {
		return R.string.label_html;
	}

	@Override
	public void setContext(Context ctx) {
		context = ctx;
	}
}
