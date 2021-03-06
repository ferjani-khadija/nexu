/**
 * © Nowina Solutions, 2015-2015
 *
 * Concédée sous licence EUPL, version 1.1 ou – dès leur approbation par la Commission européenne - versions ultérieures de l’EUPL (la «Licence»).
 * Vous ne pouvez utiliser la présente œuvre que conformément à la Licence.
 * Vous pouvez obtenir une copie de la Licence à l’adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation légale ou contractuelle écrite, le logiciel distribué sous la Licence est distribué «en l’état»,
 * SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques spécifiques relevant de la Licence.
 */
package lu.nowina.nexu.https;

import java.io.File;
import java.net.InetAddress;

import lu.nowina.nexu.api.AppConfig;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import lu.nowina.nexu.jetty.AbstractJettyServer;
import lu.nowina.nexu.jetty.JettyListAwareServerConnector;

public class JettyHttpsServer extends AbstractJettyServer {

	@Override
	public Connector[] getConnectors() {
		// HTTP connector
		final HttpConfiguration http = new HttpConfiguration();
		final JettyListAwareServerConnector connector = new JettyListAwareServerConnector(getServer());
		connector.addConnectionFactory(new HttpConnectionFactory(http));
		connector.setPorts(getApi().getAppConfig().getBindingPorts());
		connector.setHost(InetAddress.getLoopbackAddress().getCanonicalHostName());

		// HTTPS connector
		final HttpConfiguration https = new HttpConfiguration();
		https.addCustomizer(new SecureRequestCustomizer());

		// Configuring SSL
		final SslContextFactory sslContextFactory = new SslContextFactory();
		// Defining keystore path and passwords
		final File nexuHome = getApi().getAppConfig().getNexuHome();
		final File keystore = new File(nexuHome, "keystore.jks");
		sslContextFactory.setKeyStorePath(keystore.toURI().toString());
		sslContextFactory.setKeyStorePassword("password");
		sslContextFactory.setKeyManagerPassword("password");
		// Configuring the connector
		final JettyListAwareServerConnector sslConnector = new JettyListAwareServerConnector(getServer(),
				new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
		sslConnector.setPorts((getApi().getAppConfig()).getBindingPortsHttps());
		sslConnector.setHost(InetAddress.getLoopbackAddress().getCanonicalHostName());
		
		return new Connector[] {connector, sslConnector};
	}
}
