/*
 * Mule ESB Maven Tools
 * <p>
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * <p>
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tools.client.fabric;

import com.google.gson.Gson;
import org.mule.tools.client.AbstractMuleClient;
import org.mule.tools.client.fabric.model.DeploymentDetailedResponse;
import org.mule.tools.client.fabric.model.DeploymentGenericResponse;
import org.mule.tools.client.fabric.model.DeploymentModify;
import org.mule.tools.client.fabric.model.DeploymentRequest;
import org.mule.tools.client.fabric.model.Deployments;
import org.mule.tools.model.anypoint.RuntimeFabricDeployment;
import org.mule.tools.utils.DeployerLog;

import javax.ws.rs.core.Response;

import java.util.function.Supplier;

import static java.lang.String.format;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.mule.tools.client.arm.ArmClient.BASE_HYBRID_API_PATH;

public class RuntimeFabricClient extends AbstractMuleClient {

  public static final String API_VERSION = "/v2";
  public static final String HYBRID_API = BASE_HYBRID_API_PATH + API_VERSION;
  public static final String RESOURCES_PATH = HYBRID_API + "/organizations/%s/environments/%s";
  public static final String DEPLOYMENTS_PATH = RESOURCES_PATH + "/deployments";


  public RuntimeFabricClient(RuntimeFabricDeployment runtimeFabricDeployment, DeployerLog log) {
    super(runtimeFabricDeployment, log);
  }

  /**
   * Look up all the deployments
   *
   * @return {@link Deployments}
   */
  public Deployments getDeployments() {
    Response response = get(baseUri, getPathSupplier());
    checkResponseStatus(response, OK);
    return response.readEntity(Deployments.class);
  }

  /**
   * Create a new deployment
   *
   * @return {@link DeploymentDetailedResponse}
   */
  public DeploymentDetailedResponse deploy(DeploymentRequest request) {
    Response response = post(baseUri, getPathSupplier(), new Gson().toJson(request));
    checkResponseStatus(response, CREATED);
    return response.readEntity(DeploymentDetailedResponse.class);
  }

  /**
   * Update an existing deployment
   *
   * @return a list with all the {@link DeploymentGenericResponse}
   */
  public DeploymentDetailedResponse redeploy(DeploymentModify modify, String deploymentId) {
    Response response = patch(baseUri, getDeploymentPathSupplier(deploymentId), new Gson().toJson(modify));
    checkResponseStatus(response, OK);
    return response.readEntity(DeploymentDetailedResponse.class);
  }

  /**
   * Retrieves an existing deployment
   *
   * @return {@link DeploymentDetailedResponse}
   */
  public DeploymentDetailedResponse getDeployment(String deploymentId) {
    Response response = get(baseUri, getDeploymentPathSupplier(deploymentId));
    checkResponseStatus(response, OK);
    return response.readEntity(DeploymentDetailedResponse.class);
  }

  /**
   * Deletes an existing deployment
   *
   * @return {@link DeploymentDetailedResponse}
   */
  public DeploymentDetailedResponse deleteDeployment(String deploymentId) {
    Response response = get(baseUri, getDeploymentPathSupplier(deploymentId));
    checkResponseStatus(response, OK);
    return response.readEntity(DeploymentDetailedResponse.class);
  }


  private Supplier<String> getDeploymentPathSupplier(String deploymentId) {
    return () -> format(getPathSupplier().get() + "/%s", deploymentId);
  }

  public Supplier<String> getPathSupplier() {
    return () -> format(DEPLOYMENTS_PATH, getOrgId(), getEnvId());
  }
}
