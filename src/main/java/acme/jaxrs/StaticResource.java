/*
 * Copyright 2016-2018 Daniel Siviter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package acme.jaxrs;

import static java.util.logging.Level.INFO;
import static javax.ws.rs.core.MediaType.APPLICATION_SVG_XML;
import static javax.ws.rs.core.Response.ok;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource to serve static page elements.
 * 
 * @author Daniel Siviter
 * @since v1.0 [6 Aug 2018]
 */
@Path("/")
public class StaticResource {
	@Inject
	private Logger log;
	@Inject
	private ServletContext servletCtx;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response get() {
		return get(Paths.get("/index.html"));
	}

	@GET
	@Path("{path:.*}")
	@Produces({ "text/css", "application/javascript", "image/png", APPLICATION_SVG_XML, "image/x-icon" })
	public Response get(@PathParam("path") java.nio.file.Path path) {
		this.log.log(INFO, "Static resource requested: {1}", path);
		final InputStream in = this.servletCtx.getResourceAsStream(path.toString());
		if (in == null) {
			throw new NotFoundException();
		}
		return ok(in, this.servletCtx.getMimeType(path.toString())).build();
	}
}
